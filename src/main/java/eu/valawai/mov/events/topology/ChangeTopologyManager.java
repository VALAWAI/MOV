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

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.events.ListenerService;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.DeleteTopologyConnection;
import eu.valawai.mov.persistence.live.topology.EnableTopologyConnection;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
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

					switch (payload.action) {
					case REMOVE -> this.removeConnection(entity);
					case DISABLE -> this.disableConnection(entity);
					default -> this.enableConnection(entity);
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
	 * @param connection to remove.
	 */
	private void removeConnection(TopologyConnectionEntity connection) {

		DeleteTopologyConnection.fresh().withConnection(connection.id).execute().chain(deleted -> {

			if (deleted) {

				if (connection.enabled) {

					return this.closeConnection(connection);

				} else {

					return Uni.createFrom().nullItem();
				}

			} else {

				return Uni.createFrom().failure(new IllegalStateException("Not found connection to remove"));
			}

		}).subscribe().with(deleted -> {

			AddLog.fresh().withDebug().withMessage("Removed the connection {0}", connection.toLogId()).store();

		}, error -> {

			AddLog.fresh().withError(error).withMessage("Cannot remove the connection {0}", connection.toLogId())
					.store();

		});
	}

	/**
	 * Close to listen a connection.
	 *
	 * @param connection to close.
	 *
	 * @return the result if the connection is closed.
	 */
	private Uni<Void> closeConnection(TopologyConnectionEntity connection) {

		final var source = connection.source.channelName;
		return TopologyConnectionEntity
				.count(Filters.and(Filters.eq("enabled", true), Filters.eq("source.channelName", source),
						Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null))))
				.chain(count -> {

					if (count == 0 && this.listener.isOpen(source)) {

						return this.listener.close(source);

					} else {

						return Uni.createFrom().nullItem();
					}
				});
	}

	/**
	 * Called when has to disable a connection.
	 *
	 * @param connection to disable.
	 */
	private void disableConnection(TopologyConnectionEntity connection) {

		if (connection.enabled) {

			EnableTopologyConnection.fresh().withConnection(connection.id).withAction(TopologyAction.DISABLE).execute()
					.chain(any -> this.closeConnection(connection)).subscribe().with(success -> {

						AddLog.fresh().withInfo().withMessage("Disabled the connection {0}", connection.toLogId())
								.store();

					}, error -> {

						AddLog.fresh().withError(error)
								.withMessage("Cannot disable the connection {0}", connection.toLogId()).store();
					});

		} else {

			AddLog.fresh().withError().withMessage("Cannot disable the connection {0}", connection.toLogId()).store();
		}

	}

	/**
	 * Called when has to enable a connection.
	 *
	 * @param connection to enable.
	 */
	private void enableConnection(TopologyConnectionEntity connection) {

		if (!connection.enabled) {

			EnableTopologyConnection.fresh().withConnection(connection.id).withAction(TopologyAction.ENABLE).execute()
					.subscribe().with(done -> {

						AddLog.fresh().withInfo().withMessage("Enabled the connection {0}", connection.toLogId())
								.store();
						final var source = connection.source.channelName;
						if (!this.listener.isOpen(source)) {

							this.listener.toMultiBody(this.listener.openConsumer(source)).subscribe().with(msg -> {

								this.receivedMessageFromQueue(source, msg);

							}, error -> {

								AddLog.fresh().withError(error)
										.withMessage("Cannot listen for the messages from the queue {0}", source)
										.store();
							});
						}

					}, error -> {

						AddLog.fresh().withError(error)
								.withMessage("The connection {0} cannot be marked as enabled", connection.toLogId())
								.store();

					});

		} else {

			AddLog.fresh().withError().withMessage("Cannot enable the connection {0}", connection.toLogId()).store();
		}

	}

	/**
	 * Called when has received a message form a channel.
	 *
	 * @param name of the channel that has received the message
	 * @param msg  received message from the channel.
	 */
	private void receivedMessageFromQueue(String name, JsonObject msg) {

		Log.debugv("On the channel {0} received the message {1}", name, msg);
		// search for all the active connections that has the channel as source.
		final var query = Filters.and(Filters.eq("enabled", true), Filters.eq("source.channelName", name),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final Multi<TopologyConnectionEntity> search = TopologyConnectionEntity.find(query).stream();
		search.subscribe().with(connection -> {

			AddLog.fresh().withDebug().withPayload(msg)
					.withMessage("Received a message from the source of the connection {0}.", connection.toLogId())
					.store();

			final var msgToSent = this.convertSourceMessageToTargetMessage(connection, msg);
			this.publish.send(connection.target.channelName, msgToSent).subscribe().with(emptySentToTarget -> {

				AddLog.fresh().withDebug().withPayload(msgToSent)
						.withMessage("Notified to the target of the connection {0}", connection.toLogId()).store();
				if (connection.notifications != null) {
					// notify of a sent message thought a connection
					final var now = TimeManager.now();
					for (final var notification : connection.notifications) {

						if (notification.enabled) {

							final var notificationToSend = this.convertSourceMessageToNotificationMessage(connection,
									notification, msg, now);
							this.publish.send(notification.node.channelName, notificationToSend).subscribe()
									.with(emptySentToNotification -> {

										AddLog.fresh().withDebug().withPayload(notificationToSend).withMessage(
												"The message that has pass thought the connection {0}, it is notified to {1}",
												connection.toLogId(), notification.node.channelName).store();

									}, error -> {

										AddLog.fresh().withError(error).withPayload(notificationToSend).withMessage(
												"The message that has pass thought the connection {0}, it cannot be is notified to {1}",
												connection.toLogId(), notification.node.channelName).store();
									});

						} // else notification disabled
					}
				}

			}, error -> {

				AddLog.fresh().withError(error).withPayload(msgToSent)
						.withMessage("Cannot notify to the target of the connection {0}", connection.toLogId()).store();

			});

		}, error -> {

			AddLog.fresh().withError(error).withPayload(msg)
					.withMessage("Cannot process received message from the queue {0}", name).store();
		});

	}

	/**
	 * Convert the message published by a source of a connection to a message that
	 * the target of the connection is subscribed.
	 *
	 * @param connection where the message has to pas thought.
	 * @param msg        that has been published
	 */
	private JsonObject convertSourceMessageToTargetMessage(TopologyConnectionEntity connection, JsonObject msg) {

		if (connection.targetMessageConverterJSCode != null) {
			// adapt the msg to send to the notification target

		}
		return msg;
	}

	/**
	 * Convert the message published by a source of a connection to a message that
	 * the notification target is subscribed.
	 *
	 * @param connection   where the message has to pas thought.
	 * @param notification that has to be done.
	 * @param msg          that has been published
	 * @param now          the time when the message has been sent.
	 *
	 * @return the message that has to be sent
	 */
	private Object convertSourceMessageToNotificationMessage(TopologyConnectionEntity connection,
			TopologyConnectionNotification notification, JsonObject msg, long now) {

		final var payload = new SentMessagePayload();
		payload.source = new MinComponentPayload();
		payload.target = new MinComponentPayload();
		payload.messagePayload = msg;
		payload.timestamp = now;
		payload.connectionId = connection.id;
		payload.source.id = connection.source.componentId;
		payload.source.type = connection.source.inferComponentType();
		payload.source.name = connection.source.inferComponentName();
		payload.target.id = connection.target.componentId;
		payload.target.type = connection.target.inferComponentType();
		payload.target.name = connection.target.inferComponentName();

		if (notification.notificationMessageConverterJSCode != null) {
			// adapt the msg to send to the notification target

		}
		return payload;
	}
}
