/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.AddTopologyConnection;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to create a new connection of the topology.
 *
 * @see ChangeTopologyPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class CreateConnectionManager {

	/**
	 * The component to manage the messages.
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
	 * Called when has to create a connection.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("create_connection")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, CreateConnectionPayload.class);
			return this.validate(payload).chain(invalid -> {

				if (invalid == null) {

					return AddTopologyConnection.fresh().withSourceChannel(payload.source.channelName)
							.withSourceComponent(payload.source.componentId)
							.withTargetChannel(payload.target.channelName)
							.withTargetComponent(payload.target.componentId).withEnabled(false).execute()
							.map(connectionId -> {

								if (connectionId != null) {

									if (payload.enabled) {

										final var change = new ChangeTopologyPayload();
										change.action = TopologyAction.ENABLE;
										change.connectionId = connectionId;
										this.publish.send(this.changeTopologyQueueName, change).subscribe()
												.with(done -> {

													Log.debugv("Sent enable the connection {0}", connectionId);

												}, error -> {

													Log.errorv(error, "Cannot enable the connection {0}", connectionId);
												});

									}
									AddLog.fresh().withInfo().withMessage("Created the connection {0}.", connectionId)
											.withPayload(content).store();

									return null;

								} else {

									return new IllegalArgumentException("Cannot store the connection");
								}
							});

				} else {

					return Uni.createFrom().item(invalid);
				}

			}).subscribeAsCompletionStage().thenCompose(error -> {

				if (error == null) {

					return msg.ack();

				} else {

					AddLog.fresh().withError(error).withMessage("Received invalid change topology payload.")
							.withPayload(content).store();

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
	 * Check that the connection is valid.
	 *
	 * @param payload with the connection to validate.
	 *
	 * @return null of the connection is valid or the error that explains why is not
	 *         valid.
	 *
	 * @see #validateSource(CreateConnectionPayload)
	 * @see #validateTarget(CreateConnectionPayload)
	 */
	private Uni<Throwable> validate(CreateConnectionPayload payload) {

		return this.validateSource(payload).chain(error -> {

			if (error == null) {

				return this.validateTarget(payload);

			} else {

				return Uni.createFrom().item(error);
			}
		});
	}

	/**
	 * Check that the source of the connection is valid.
	 *
	 * @param payload with the connection to validate.
	 *
	 * @return null of the connection source is valid or the error that explains why
	 *         is not valid.
	 */
	private Uni<Throwable> validateSource(CreateConnectionPayload payload) {

		final Uni<ComponentEntity> findSource = ComponentEntity.findById(payload.source.componentId);
		return findSource.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the source component.");
			return null;

		}).map(value -> {

			if (value == null) {

				return new IllegalArgumentException("The source component is not defined");

			} else {

				final var source = value;
				if (source.channels != null) {

					for (final var sourceChannel : source.channels) {

						if (sourceChannel.name.equals(payload.source.channelName) && sourceChannel.publish != null) {

							return null;
						}
					}
				}
				return new IllegalArgumentException("The source component does not publish on the channel name");

			}

		});
	}

	/**
	 * Check that the target of the connection is valid.
	 *
	 * @param payload with the connection to validate.
	 *
	 * @return null of the connection target is valid or the error that explains why
	 *         is not valid.
	 */
	private Uni<Throwable> validateTarget(CreateConnectionPayload payload) {

		final Uni<ComponentEntity> findTarget = ComponentEntity.findById(payload.target.componentId);
		return findTarget.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the target component.");
			return null;

		}).map(value -> {

			if (value == null) {

				return new IllegalArgumentException("The target component is not defined");

			} else {

				final var target = value;
				if (target.channels != null) {

					for (final var targetChannel : target.channels) {

						if (targetChannel.name.equals(payload.target.channelName) && targetChannel.subscribe != null) {

							return null;
						}
					}
				}
				return new IllegalArgumentException("The target component does not publish on the channel name");

			}

		});
	}

}
