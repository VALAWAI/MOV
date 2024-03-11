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

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.ChangeTopologyPayload;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.components.FinishComponent;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
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
			final Uni<ComponentEntity> find = ComponentEntity.findById(payload.componentId);
			return find.onFailure().recoverWithItem(error -> {

				Log.errorv("Cannot obtain the component {0}", payload.componentId);
				return null;

			}).subscribeAsCompletionStage().thenCompose(component -> {

				if (component != null && component.finishedTime == null) {

					final var paginator = TopologyConnectionEntity
							.find("source.componentId = ?1 or target.componentId = ?1", component.id)
							.page(Page.ofSize(10));
					this.closeConnectionsOf(payload, paginator);
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

	/**
	 * Close the connections of the paginator.
	 *
	 * @param payload   with the component to close the connections.
	 * @param paginator function that paginate the connections to close.
	 */
	private void closeConnectionsOf(UnregisterComponentPayload payload,
			ReactivePanacheQuery<ReactivePanacheMongoEntityBase> paginator) {

		paginator.hasNextPage().subscribe().with(hasNext -> {

			if (hasNext) {

				final Multi<TopologyConnectionEntity> pageGetter = paginator.nextPage().stream();
				pageGetter.onCompletion().invoke(() -> {

					this.closeConnectionsOf(payload, paginator);

				}).subscribe().with(connection -> {

					final var msg = new ChangeTopologyPayload();
					msg.action = TopologyAction.REMOVE;
					msg.connectionId = connection.id;
					this.publish.send(this.changeTopologyQueueName, msg).subscribe().with(done -> {

						Log.debugv("Sent remove the connection {0}", connection.id);

					}, error -> {

						Log.errorv(error, "Cannot send remove connection {0}", connection.id);
					});

				}, error -> {

					this.finishedComponent(payload);
					Log.errorv(error, "Error when get the connections to close.");

				});

			} else {

				this.finishedComponent(payload);

			}

		}, error -> {

			this.finishedComponent(payload);
			Log.errorv(error, "Error when paginate the components to close.");

		});

	}

	/**
	 * Mark as finished the component.
	 *
	 * @param payload with the component to mark as finished.
	 */
	private void finishedComponent(UnregisterComponentPayload payload) {

		FinishComponent.fresh().withComponent(payload.componentId).execute().subscribe().with(done -> {

			if (done) {

				AddLog.fresh().withInfo().withMessage("Unregistered the component {0}.", payload.componentId)
						.withPayload(payload).store();

			} else {
				AddLog.fresh().withError().withMessage("Cannot unregister the component {0}.", payload.componentId)
						.withPayload(payload).store();

			}
		});
	}

}
