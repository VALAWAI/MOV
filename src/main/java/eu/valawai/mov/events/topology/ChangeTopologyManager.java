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
import eu.valawai.mov.persistence.live.topology.GetTopologyConnection;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
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

					final var source = entity.source.channelName;
					final var target = entity.target.channelName;
					final var connectionLog = entity.id.toHexString() + " ( from '" + source + "' to '" + target + "')";
					switch (payload.action) {
					case REMOVE -> this.removeConnection(entity, connectionLog);
					case DISABLE -> this.disableConnection(entity, connectionLog);
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
	 * @param connection    to remove.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void removeConnection(TopologyConnectionEntity connection, String connectionLog) {

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

			AddLog.fresh().withDebug().withMessage("Removed the connection {0}", connectionLog).store();

		}, error -> {

			AddLog.fresh().withError(error).withMessage("Cannot remove the connection {0}", connectionLog).store();

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
	 * @param connection    to disable.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void disableConnection(TopologyConnectionEntity connection, String connectionLog) {

		if (connection.enabled) {

			EnableTopologyConnection.fresh().withConnection(connection.id).withAction(TopologyAction.DISABLE).execute()
					.chain(any -> this.closeConnection(connection)).subscribe().with(success -> {

						AddLog.fresh().withInfo().withMessage("Disabled the connection {0}", connectionLog).store();

					}, error -> {

						AddLog.fresh().withError(error).withMessage("Cannot disable the connection {0}", connectionLog)
								.store();
					});

		} else {

			AddLog.fresh().withError().withMessage("Cannot disable the connection {0}", connectionLog).store();
		}

	}

	/**
	 * Called when has to enable a connection.
	 *
	 * @param connection    to enable.
	 * @param connectionLog identifier of the connection on the log message.
	 */
	private void enableConnection(TopologyConnectionEntity connection, String connectionLog) {

		if (!connection.enabled) {

			EnableTopologyConnection.fresh().withConnection(connection.id).withAction(TopologyAction.ENABLE).execute()
					.subscribe().with(done -> {

						AddLog.fresh().withInfo().withMessage("Enabled the connection {0}", connectionLog).store();

					}, error -> {

						AddLog.fresh().withError(error)
								.withMessage("Opened connection {0}, but not marked as enabled", connectionLog).store();

					});
			final var source = connection.source.channelName;
			if (!this.listener.isOpen(source)) {

				this.listener.toMultiBody(this.listener.openConsumer(source)).subscribe().with(received -> {

					this.handleConnectionMessage(source, received);

				}, error -> {

					AddLog.fresh().withError(error).withMessage("Cannot enable the connection {0}", connectionLog)
							.store();
				});

			}

		} else {

			AddLog.fresh().withError().withMessage("Cannot enable the connection {0}", connectionLog).store();
		}

	}

	/**
	 * Called when a message is received from a channel.
	 *
	 * @param source   channel that has received the message
	 * @param received message to send.
	 */
	private void handleConnectionMessage(String source, JsonObject received) {

		final var query = Filters.and(Filters.eq("enabled", true), Filters.eq("source.channelName", source),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final Multi<TopologyConnectionEntity> search = TopologyConnectionEntity.find(query).stream();
		search.subscribe().with(connection -> {

			final var connectionLog = connection.id.toHexString() + " ( from '" + source + "' to '"
					+ connection.target.channelName + "')";
			this.handleConnectionMessage(connection, received, connectionLog);

		}, error -> {

			AddLog.fresh().withError(error).withPayload(received)
					.withMessage("Cannot process received message from the queue {0}", source).store();
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

			AddLog.fresh().withDebug().withMessage("Sent a message through the connection {0}", connectionLog)
					.withPayload(received).store();

			if (connection.c2Subscriptions != null) {

				final var payload = new SentMessagePayload();
				payload.source = new MinComponentPayload();
				payload.target = new MinComponentPayload();
				payload.messagePayload = received;
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
