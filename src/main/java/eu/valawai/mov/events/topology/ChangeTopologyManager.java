/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.concurrent.CompletionStage;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.events.ListenerService;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.DeleteTopologyConnection;
import eu.valawai.mov.persistence.topology.EnableTopologyConnection;
import eu.valawai.mov.persistence.topology.GetTopologyConnection;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
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
	 * The component to send events.
	 */
	@Inject
	EventBus bus;

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
					switch (payload.action) {
					case REMOVE -> this.removeConnection(payload.connectionId, source, connectionLog);
					case DISABLE -> this.disableConnection(payload.connectionId, source, connectionLog);
					default -> this.enableConnection(entity, connectionLog);
					}
					return null;
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

	/**
	 * Called when has to remove a connection.
	 *
	 * @param connectionId  the identifier of the connection to remove.
	 * @param source        channel of the connection.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void removeConnection(ObjectId connectionId, String source, String connectionLog) {

		DeleteTopologyConnection.fresh().withConnection(connectionId).execute()
				.chain(deleted -> this.listener.close(source)).subscribe().with(success -> {

					AddLog.fresh().withInfo().withMessage("Removed the connection {0}", connectionLog).store();

				}, error -> {

					AddLog.fresh().withError(error).withMessage("Cannot remove the connection {0}", connectionLog)
							.store();
				});
	}

	/**
	 * Called when has to disable a connection.
	 *
	 * @param connectionId  the identifier of the connection to remove.
	 * @param source        channel of the connection.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void disableConnection(ObjectId connectionId, String source, String connectionLog) {

		this.listener.close(source).chain(any -> {

			return EnableTopologyConnection.fresh().withConnection(connectionId).withAction(TopologyAction.DISABLE)
					.execute();

		}).subscribe().with(success -> {

			if (success) {

				AddLog.fresh().withInfo().withMessage("Disabled the connection {0}", connectionLog).store();

			} else {

				AddLog.fresh().withError()
						.withMessage("Disabled the connection {0}, but not market as disabled", connectionLog).store();

			}

		}, error -> {

			AddLog.fresh().withError(error).withMessage("Cannot disable the connection {0}", connectionLog).store();
		});
	}

	/**
	 * Called when has to enable a connection.
	 *
	 * @param connection    to enable.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void enableConnection(TopologyConnectionEntity connection, String connectionLog) {

		final var source = connection.source.channelName;
		this.listener.toMultiBody(this.listener.openConsumer(source).onItem().invoke(consumer -> {

			EnableTopologyConnection.fresh().withConnection(connection.id).withAction(TopologyAction.ENABLE).execute()
					.subscribe().with(done -> {

						AddLog.fresh().withInfo().withMessage("Enabled the connection {0}", connectionLog).store();

					}, error -> {

						AddLog.fresh().withError(error)
								.withMessage("Opened connection {0}, but not marked as enabled", connectionLog).store();

					});

		})).subscribe().with(received -> {

			this.handleConnectionMessage(connection, received, connectionLog);

		}, error -> {

			AddLog.fresh().withError(error).withMessage("Cannot enable the connection {0}", connectionLog).store();
		});

	}

	/**
	 * Called when a message is received from the source of a connection.
	 *
	 * @param connection    where the message is received.
	 * @param received      the message that has published by the source.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void handleConnectionMessage(TopologyConnectionEntity connection, JsonObject received,
			String connectionLog) {

		final var target = connection.target.channelName;
		this.publish.send(target, received).subscribe().with(done -> {

			AddLog.fresh().withInfo().withMessage("Sent a message through the connection {0}", connectionLog)
					.withPayload(received).store();

			if (connection.c2Subscriptions != null) {

				final var payload = new SentMessagePayload();
				payload.source = new MinComponentPayload();
				payload.target = new MinComponentPayload();
				payload.content = received;
				payload.timestamp = TimeManager.now();
				payload.connectionId = connection.id;

				GetTopologyConnection.fresh().withConnection(connection.id).execute().subscribe().with(model -> {

					payload.source.id = model.source.component.id;
					payload.source.type = model.source.component.type;
					payload.source.name = model.source.component.name;
					payload.target.id = model.target.component.id;
					payload.target.type = model.target.component.type;
					payload.target.name = model.target.component.name;
					for (final var subscriber : connection.c2Subscriptions) {

						this.publish.send(subscriber.channelName, payload).subscribe().with(sent -> {

							AddLog.fresh().withDebug().withMessage(
									"Component {0} at {1} has notified of a message that has been send on the connection {2}",
									subscriber.componentId, subscriber.channelName, connectionLog).withPayload(payload)
									.store();

						}, error -> {

							AddLog.fresh().withError(error).withMessage(
									"Cannot notify to component {0} at {1} that a message has been send on the connection {2}",
									subscriber.componentId, subscriber.channelName, connectionLog).withPayload(payload)
									.store();

						});

					}

				}, error -> {

					AddLog.fresh().withError(error)
							.withMessage("Cannot found the information the connection {0}", connectionLog)
							.withPayload(payload).store();
				});
			}

		}, error -> {

			AddLog.fresh().withError(error)
					.withMessage("Cannot send a message thought the connection {0}", connectionLog)
					.withPayload(received).store();
		});
	}

}
