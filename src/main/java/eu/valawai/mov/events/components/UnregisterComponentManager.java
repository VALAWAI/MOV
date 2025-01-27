/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.ChangeTopologyPayload;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.components.FinishComponent;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.RemoveAllC2SubscriptionByComponent;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the un-registration of a component.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class UnregisterComponentManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	protected PayloadService service;

	/**
	 * Notify that a new connection is created.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.change_topology.queue.name", defaultValue = "valawai/topology/change")
	String changeTopologyQueueName;

	/**
	 * The service to send messages.
	 */
	@Inject
	PublishService publish;

	/**
	 * Called when has to unregister a component.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("unregister_component")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, UnregisterComponentPayload.class);
			return FinishComponent.fresh().withComponent(payload.componentId).execute().chain(finished -> {

				if (finished) {

					return RemoveAllC2SubscriptionByComponent.fresh().withComponent(payload.componentId).execute()
							.map(removed -> {
								Log.debugv("Unsubscribed {0} from {1} connections.", payload.componentId, finished);
								return true;
							});

				} else {

					return Uni.createFrom().item(false);

				}

			}).subscribeAsCompletionStage().thenCompose(finished -> {

				if (finished) {

					final var query = Filters.and(
							Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
							Filters.or(Filters.eq("source.componentId", payload.componentId),
									Filters.eq("target.componentId", payload.componentId)));
					final Multi<TopologyConnectionEntity> findConnections = TopologyConnectionEntity.find(query)
							.stream();
					findConnections.subscribe().with(connection -> {

						final var changePayload = new ChangeTopologyPayload();
						changePayload.action = TopologyAction.REMOVE;
						changePayload.connectionId = connection.id;
						this.publish.send(this.changeTopologyQueueName, changePayload).subscribe().with(done -> {

							Log.debugv("Sent remove the connection {0}", connection.id);

						}, error -> {

							Log.errorv(error, "Cannot send remove connection {0}", connection.id);
						});

					});
					AddLog.fresh().withInfo().withMessage("Unregistered the component {0}.", payload.componentId)
							.withPayload(payload).store();
					return msg.ack();

				} else {

					AddLog.fresh().withError()
							.withMessage("Cannot unregister the undefined component {0}.", payload.componentId)
							.withPayload(payload).store();
					return msg.nack(new IllegalArgumentException("Not found component with the specified identifier."));
				}
			});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid register component payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}

}
