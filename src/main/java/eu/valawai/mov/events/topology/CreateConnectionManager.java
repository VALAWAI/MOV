/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.concurrent.CompletionStage;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.AddC2SubscriptionToTopologyConnection;
import eu.valawai.mov.persistence.topology.AddTopologyConnection;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
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
	 * The pattern that has to match a channel name to be used as subscriber.
	 */
	private static final String C2_SUBSCRIBER_CHANNEL_NAME_PATTERN = "valawai/c2/\\w+/control/\\w+";

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

			final var context = new ManagerContext();
			context.payload = this.service.decodeAndVerify(content, CreateConnectionPayload.class);
			return this.validate(context).chain(invalid -> {

				if (invalid == null) {

					return AddTopologyConnection.fresh().withSourceChannel(context.payload.source.channelName)
							.withSourceComponent(context.payload.source.componentId)
							.withTargetChannel(context.payload.target.channelName)
							.withTargetComponent(context.payload.target.componentId).withEnabled(false).execute()
							.chain(connectionId -> {

								if (connectionId != null) {

									AddLog.fresh().withInfo().withMessage("Created the connection {0}.", connectionId)
											.withPayload(content).store();
									context.connectionId = connectionId;
									final var paginator = ComponentEntity
											.find("type = ?1 and channels.subscribe != null and channels.name like ?2",
													Sort.ascending("_id"), ComponentType.C2,
													C2_SUBSCRIBER_CHANNEL_NAME_PATTERN)
											.page(Page.ofSize(10));
									this.checkSubscriptionAndEnable(context, paginator);
									return Uni.createFrom().nullItem();

								} else {

									return Uni.createFrom()
											.item(new IllegalArgumentException("Cannot store the connection"));
								}

							});

				} else {

					return Uni.createFrom().item(invalid);
				}

			}).onFailure().recoverWithItem(error -> error).subscribeAsCompletionStage().thenCompose(error -> {

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
	 * @param context with the payload to validate.
	 *
	 * @return null of the connection is valid or the error that explains why is not
	 *         valid.
	 *
	 * @see #validateSource(ManagerContext)
	 * @see #validateTarget(ManagerContext)
	 */
	private Uni<Throwable> validate(ManagerContext context) {

		return this.validateSource(context).chain(error -> {

			if (error == null) {

				return this.validateTarget(context).chain(error2 -> {

					if (error2 == null) {

						if (context.sourceChannel.publish.match(context.targetChannel.subscribe)) {

							return Uni.createFrom().nullItem();

						} else {

							return Uni.createFrom().failure(new IllegalArgumentException(
									"The source payload does not match the target payload."));
						}

					} else {

						return Uni.createFrom().item(error2);
					}
				});

			} else {

				return Uni.createFrom().item(error);
			}
		});
	}

	/**
	 * Check that the source of the connection is valid.
	 *
	 * @param context with the payload to validate.
	 *
	 * @return null of the connection source is valid or the error that explains why
	 *         is not valid.
	 */
	private Uni<Throwable> validateSource(ManagerContext context) {

		final Uni<ComponentEntity> findSource = ComponentEntity.findById(context.payload.source.componentId);
		return findSource.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the source component.");
			return null;

		}).map(source -> {

			if (source == null) {

				return new IllegalArgumentException("The source component is not defined");

			} else {

				if (source.channels != null) {

					for (final var sourceChannel : source.channels) {

						if (sourceChannel.name.equals(context.payload.source.channelName)
								&& sourceChannel.publish != null) {

							context.sourceChannel = sourceChannel;
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
	 * @param context with the payload to validate.
	 *
	 * @return null of the connection target is valid or the error that explains why
	 *         is not valid.
	 */
	private Uni<Throwable> validateTarget(ManagerContext context) {

		final Uni<ComponentEntity> findTarget = ComponentEntity.findById(context.payload.target.componentId);
		return findTarget.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the target component.");
			return null;

		}).map(target -> {

			if (target == null) {

				return new IllegalArgumentException("The target component is not defined");

			} else {

				if (target.channels != null) {

					for (final var targetChannel : target.channels) {

						if (targetChannel.name.equals(context.payload.target.channelName)
								&& targetChannel.subscribe != null) {

							context.targetChannel = targetChannel;
							return null;
						}
					}
				}
				return new IllegalArgumentException("The target component does not publish on the channel name");

			}

		});
	}

	/**
	 * Context used by this manager.
	 */
	private class ManagerContext {

		/**
		 * The received payload.
		 */
		public CreateConnectionPayload payload;

		/**
		 * The source channel of the source component.
		 */
		public ChannelSchema sourceChannel;

		/**
		 * The target channel of the target component.
		 */
		public ChannelSchema targetChannel;

		/**
		 * The identifier of the added connection.
		 */
		public ObjectId connectionId;

	}

	/**
	 * Check for the necessary subscription to the
	 */
	private void checkSubscriptionAndEnable(ManagerContext context,
			ReactivePanacheQuery<ReactivePanacheMongoEntityBase> paginator) {

		final Multi<ComponentEntity> getter = paginator.stream();
		getter.onCompletion().invoke(() -> {

			paginator.hasNextPage().subscribe().with(hasNext -> {

				if (hasNext) {

					this.checkSubscriptionAndEnable(context, paginator.nextPage());

				} else {

					this.enableConnectionIfNecessary(context);

				}

			}, error -> {

				Log.errorv(error,
						"Error when paginate the components to check if has to be subscribed into a connection.");
				this.enableConnectionIfNecessary(context);

			});

		}).subscribe().with(target -> {

			this.checkSubscription(context, target);

		}, error -> {

			Log.errorv(error, "Error when checking witch component may be subscribed to the connection messages.");
			this.enableConnectionIfNecessary(context);

		});

	}

	/**
	 * Enable the created connection if it is necessary.
	 *
	 * @param context with the created connection.
	 */
	private void enableConnectionIfNecessary(ManagerContext context) {

		if (context.payload.enabled) {

			final var change = new ChangeTopologyPayload();
			change.action = TopologyAction.ENABLE;
			change.connectionId = context.connectionId;
			this.publish.send(this.changeTopologyQueueName, change).subscribe().with(done -> {

				Log.debugv("Sent enable the connection {0}", context.connectionId);

			}, error -> {

				Log.errorv(error, "Cannot enable the connection {0}", context.connectionId);
			});

		}

	}

	/**
	 * Check if a component must be subscribed to the created connection.
	 *
	 * @param context with the created connection.
	 * @param target  component to check it it has to be subscribed into the
	 *                connection.
	 */
	private void checkSubscription(ManagerContext context, ComponentEntity target) {

		for (final var channel : target.channels) {

			if (channel.name.matches(C2_SUBSCRIBER_CHANNEL_NAME_PATTERN) && channel.subscribe != null
					&& channel.subscribe.match(context.sourceChannel.publish)) {
				// the component must be subscribed into the connection
				AddC2SubscriptionToTopologyConnection.fresh().withConnection(context.connectionId)
						.withComponent(target.id).withChannel(channel.name).execute().subscribe().with(success -> {

							if (success) {

								AddLog.fresh().withDebug().withMessage(
										"Subscribed the channel {0} of the component {1} into the connection {2}.",
										channel.name, target.id, context.connectionId).store();

							} else {

								AddLog.fresh().withError().withMessage(
										"Could not subscribe the channel {0} of the component {1} into the connection {2}.",
										channel.name, target.id, context.connectionId).store();
							}
						});

			}
		}

	}

}
