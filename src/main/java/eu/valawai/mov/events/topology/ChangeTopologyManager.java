/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.events.ListenerService;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.EnableTopologyConnection;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the changes on the topology.
 *
 * @see ChangeTopologyPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ChangeTopologyManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	PublishService publish;

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	ListenerService listener;

	/**
	 * Called when has to register a component.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("change_topology")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, ChangeTopologyPayload.class);
			final Uni<TopologyConnectionEntity> find = TopologyConnectionEntity.findById(payload.connectionId);
			final Uni<Throwable> startLink = find.onFailure().recoverWithItem(error -> {

				Log.errorv(error, "Cannot search for the connection {0}", payload.connectionId);
				return null;

			}).map(entity -> {

				if (entity == null) {

					AddLog.fresh().withError()
							.withMessage("Received change topology payload for an undefined connection.")
							.withPayload(content).store();
					return new IllegalArgumentException("No connection associated to the identifier");

				} else {

					final var source = entity.source.channelName;
					final var target = entity.target.channelName;
					final var connectionLog = entity.id.toHexString() + " ( from '" + source + "' to '" + target + "')";
					if (payload.action == TopologyAction.DISABLE) {

						this.listener.close(source).chain(any -> {

							return EnableTopologyConnection.fresh().withConnection(payload.connectionId)
									.withAction(payload.action).execute();

						}).subscribe().with(success -> {

							if (success) {

								AddLog.fresh().withInfo().withMessage("Disabled the connection {0}", connectionLog)
										.store();

							} else {

								AddLog.fresh().withError()
										.withMessage("Disabled the connection {0}, but not market as disabled",
												connectionLog)
										.store();

							}

						}, error -> {

							AddLog.fresh().withError(error)
									.withMessage("Cannot disable the connection {0}", connectionLog).store();
						});
						return null;

					} else {

						this.listener.toMultiBody(this.listener.openConsumer(source).onItem().invoke(consumer -> {

							EnableTopologyConnection.fresh().withConnection(payload.connectionId)
									.withAction(payload.action).execute().subscribe().with(done -> {

										AddLog.fresh().withInfo()
												.withMessage("Enabled the connection {0}", connectionLog).store();

									}, error -> {

										AddLog.fresh().withError(error)
												.withMessage("Opened connection {0}, but not marked as enabled",
														connectionLog)
												.store();

									});

						})).subscribe().with(received -> {

							this.publish.send(target, received).subscribe().with(done -> {

								AddLog.fresh().withInfo().withMessage("Sent a message from {0} to {1}", source, target)
										.withPayload(received).store();

							}, error -> {

								AddLog.fresh().withError(error)
										.withMessage("Cannot send a message from {0} to {1}", source, target)
										.withPayload(received).store();
							});

						}, error -> {

							AddLog.fresh().withError(error)
									.withMessage("Cannot enable the connection {0}", connectionLog).store();
						});

						return null;
					}
				}
			});
			return startLink.subscribeAsCompletionStage().thenCompose(error -> {

				if (error == null) {

					return msg.ack();

				} else {

					return msg.nack(error);
				}
			});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid change topology payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}

}
