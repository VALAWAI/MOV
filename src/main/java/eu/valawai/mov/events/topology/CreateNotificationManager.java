/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.HashMap;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
import eu.valawai.mov.persistence.live.topology.UpsertNotificationToTopologyConnection;
import eu.valawai.mov.services.LocalConfigService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to create a new notification into a connection of the
 * topology.
 *
 * @see CreateNotificationPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class CreateNotificationManager {

	/**
	 * The component to extract the information of a receive a messages from a
	 * broker.
	 */
	@Inject
	PayloadService service;

	/**
	 * Notify that a new connection is created.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.change_topology.queue.name", defaultValue = "valawai/topology/change")
	String changeTopologyQueueName;

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	PublishService publish;

	/**
	 * The local configuration.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * Called when has to create a connection.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("create_notification")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, CreateNotificationPayload.class);

			return this.validateTarget(payload).chain(any -> {

				final var notification = new TopologyConnectionNotification();
				notification.notificationMessageConverterJSCode = payload.converterJSCode;
				notification.enabled = payload.enabled;
				notification.node = new TopologyNode();
				notification.node.componentId = payload.target.componentId;
				notification.node.channelName = payload.target.channelName;
				return UpsertNotificationToTopologyConnection.fresh().withConnection(payload.connectionId)
						.withNotification(notification).execute()
						.chain(upserted -> upserted ? Uni.createFrom().nullItem()
								: Uni.createFrom()
										.failure(new IllegalArgumentException("Cannot create the notification")));

			}).map(any -> (Throwable) any).onFailure().recoverWithItem(failure -> failure).subscribeAsCompletionStage()
					.thenCompose(error -> {
						if (error == null) {

							AddLog.fresh().withDebug().withMessage("Created notification").withPayload(payload).store();
							return msg.ack();

						} else {

							AddLog.fresh().withError(error).withPayload(payload).store();
							return msg.nack(error);
						}
					});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid create topology connection payload.")
					.withPayload(content).store();
			return msg.nack(error);
		}

	}

	/**
	 * Check that the target of the notification to create is valid.
	 *
	 * @param payload with the notification to create.
	 *
	 * @return null of the connection target is valid or the error that explains why
	 *         is not valid.
	 */
	private Uni<Void> validateTarget(CreateNotificationPayload payload) {

		final Uni<ComponentEntity> findTarget = ComponentEntity.findById(payload.target.componentId);
		return findTarget.chain(target -> {

			if (target == null) {

				return Uni.createFrom()
						.failure(new IllegalArgumentException("The notification target component is not defined"));

			} else {

				if (target.channels != null) {

					for (final var targetChannel : target.channels) {

						if (targetChannel.name.equals(payload.target.channelName) && targetChannel.subscribe != null) {

							if (payload.converterJSCode != null) {

								return Uni.createFrom().nullItem();

							} else {

								return this.validateSourceToTargetCompatibility(payload, targetChannel.subscribe);
							}
						}
					}
				}
				return Uni.createFrom().failure(new IllegalArgumentException(
						"The notification target component does not subscribe on the channel name"));
			}
		});
	}

	/**
	 * Check that the target of the notification to create is valid.
	 *
	 * @param payload       with the notification to create.
	 * @param targetPayload the definition of the message that the target
	 *                      notification expects.
	 *
	 * @return null of the source message schema is compatible with the target
	 *         channel.
	 */
	private Uni<Void> validateSourceToTargetCompatibility(CreateNotificationPayload payload,
			PayloadSchema targetPayload) {

		final Uni<TopologyConnectionEntity> findConnection = TopologyConnectionEntity.findById(payload.connectionId);
		return findConnection.chain(connection -> {

			if (connection == null) {

				return Uni.createFrom().failure(new IllegalArgumentException("The connection is not defined"));

			} else {

				final Uni<ComponentEntity> findSource = ComponentEntity.findById(connection.source.componentId);
				return findSource.chain(source -> {

					if (source == null) {

						return Uni.createFrom().failure(
								new IllegalArgumentException("The notification source component is not defined"));

					} else {

						if (source.channels != null) {

							for (final var sourceChannel : source.channels) {

								if (sourceChannel.name.equals(connection.source.channelName)
										&& sourceChannel.publish != null) {

									if (sourceChannel.publish.match(targetPayload, new HashMap<>())) {

										return Uni.createFrom().nullItem();

									} else {

										final var notificationPayload = SentMessagePayload
												.createSentMessagePayloadSchemaFor(sourceChannel.publish);

										if (notificationPayload.match(targetPayload, new HashMap<>())) {
											// It match the previous notification payload format
											return Uni.createFrom().nullItem();
										}
									}
								}
							}
						}
						return Uni.createFrom().failure(new IllegalArgumentException(
								"The notification message is not compatible with the target channel"));
					}
				});
			}
		});
	}

}
