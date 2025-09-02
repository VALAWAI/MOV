/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import eu.valawai.mov.MOVConfiguration;
import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.CreateConnectionPayload;
import eu.valawai.mov.events.topology.NodePayload;
import eu.valawai.mov.events.topology.SentMessagePayload;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import eu.valawai.mov.persistence.live.topology.TopologyNode;
import eu.valawai.mov.persistence.live.topology.UpsertNotificationToTopologyConnection;
import eu.valawai.mov.services.LocalConfigService;
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
 * The element used to manage the registration of a component.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class RegisterComponentManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	protected PayloadService service;

	/**
	 * The component to send the message to create a topology connection.
	 */
	@Inject
	@Channel("send_create_connection")
	Emitter<CreateConnectionPayload> create;

	/**
	 * The service to send messages.
	 */
	@Inject
	PublishService publish;

	/**
	 * The local configuration.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * The keys of the payload to notify that a component is registered.
	 */
	private static final String[] REGISTERED_PAYLOAD_FIELD_NAMES = { "id", "name", "description", "version",
			"api_version", "type", "since", "channels" };

	/**
	 * The keys of the payload to notify that a message has been sent thought a
	 * topology connection.
	 */
	private static final String[] SENT_MESSAGE_PAYLOAD_FIELD_NAMES = { "connection_id", "source", "target",
			"message_payload", "timestamp" };

	/**
	 * Called when has to register a component.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("register_component")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var context = new ManagerContext(
					this.service.decodeAndVerify(content, RegisterComponentPayload.class));
			context.behaviour = this.configuration.getPropertyValue(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME,
					TopologyBehavior.class, TopologyBehavior.AUTO_DISCOVER);
			return this.validate(context).onItem().ifNull().switchTo(() -> {

				final var entity = context.createEntity();
				return entity.persist().chain(componentId -> {

					switch (context.behaviour) {
					case TopologyBehavior.AUTO_DISCOVER:
						this.autoDiscoverConnections(entity);
						break;
					default:
						// DO_NOTHING
					}

					return Uni.createFrom().nullItem();

				});

			}).onFailure().recoverWithItem(error -> error).subscribeAsCompletionStage().thenCompose(error -> {

				if (error == null) {

					return msg.ack();

				} else {

					AddLog.fresh().withError(error)
							.withMessage("Received invalid register component payload, because {0}.",
									error.getMessage())
							.withPayload(content).store();
					return msg.nack(error);
				}
			});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid register component payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}

	/**
	 * Check that the component to register is valid.
	 *
	 * @param context with the payload to validate.
	 *
	 * @return null of the component is valid or the error that explains why is not
	 *         valid.
	 */
	private Uni<Throwable> validate(ManagerContext context) {

		if (context.component == null) {

			return Uni.createFrom().failure(new IllegalArgumentException("The async API is not valid."));
		}
		return null;

	}

	/**
	 * Context used by this manager.
	 */
	private class ManagerContext {

		/**
		 * The received payload.
		 */
		public RegisterComponentPayload payload;

		/**
		 * The behaviour to do after the component has been registered.
		 */
		public TopologyBehavior behaviour;

		/**
		 * The component to be registered.
		 */
		public Component component;

		/**
		 * Create a new context
		 *
		 * @param payload with the connection to be created.
		 */
		public ManagerContext(RegisterComponentPayload payload) {

			this.payload = payload;
			this.component = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
			if (this.component != null) {

				this.component.type = payload.type;
				if (payload.name != null) {

					this.component.name = payload.name;
				}
				if (payload.version != null) {

					this.component.version = payload.version;
				}
			}
		}

		/**
		 * Create the entity to be stored.
		 *
		 * @return the entity to be stored.
		 */
		public ComponentEntity createEntity() {

			final var entity = new ComponentEntity();
			entity.name = this.component.name;
			entity.description = this.component.description;
			entity.version = this.component.version;
			entity.apiVersion = this.component.apiVersion;
			entity.type = this.component.type;
			entity.since = TimeManager.now();
			entity.channels = this.component.channels;
			return entity;
		}
	}

	/**
	 * Auto discover connections.
	 *
	 * @param source component to check to create the connection.
	 */
	private void autoDiscoverConnections(ComponentEntity source) {

		final var channelsToIgnore = new HashSet<String>();
		this.verifySpecialChannels(source, channelsToIgnore);

		if (source.channels != null && !source.channels.isEmpty()) {

			this.createConnectionsFor(source, channelsToIgnore);
		}

	}

	/**
	 * Check the special channels that can be defined on the specification.
	 *
	 * @param entity           of the component that has been registered.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 *
	 * @see #notifyComponentRegistered
	 * @see #subscribeComponentIntoConnections
	 */
	private void verifySpecialChannels(ComponentEntity entity, Collection<String> channelsToIgnore) {

		if (entity.channels != null) {

			final var expectedNamePattern = "valawai/" + entity.type.name().toLowerCase() + "/\\w+/control/\\w+";
			final var maybeSubscribeChannels = new ArrayList<ChannelSchema>();
			for (final var channel : entity.channels) {

				if (channel.subscribe instanceof final ObjectPayloadSchema schema
						&& channel.name.matches(expectedNamePattern)) {
					if (channel.name.endsWith("/registered")
							&& schema.properties.keySet().containsAll(Arrays.asList(REGISTERED_PAYLOAD_FIELD_NAMES))) {
						channelsToIgnore.add(channel.name);
						this.notifyComponentRegistered(entity, channel);

					} else if (entity.type == ComponentType.C2 && schema.properties.keySet()
							.containsAll(Arrays.asList(SENT_MESSAGE_PAYLOAD_FIELD_NAMES))) {

						channelsToIgnore.add(channel.name);
						maybeSubscribeChannels.add(channel);
					}
				}
			}

			if (!maybeSubscribeChannels.isEmpty()) {

				final var paginator = TopologyConnectionEntity
						.find("notifications.node.componentId != ?1", Sort.ascending("_id"), entity.id)
						.page(Page.ofSize(10));
				this.subscribeComponentIntoConnections(entity, maybeSubscribeChannels, paginator);
			}

		}
	}

	/**
	 * Check for some connections is the new component must be registered as
	 * subscriber.
	 *
	 * @param entity   of the component that has been registered.
	 * @param channels that may be notified when a message is pass thought a
	 *                 topology connection.
	 */
	private void subscribeComponentIntoConnections(ComponentEntity entity, List<ChannelSchema> channels,
			ReactivePanacheQuery<ReactivePanacheMongoEntityBase> paginator) {

		final Multi<TopologyConnectionEntity> getter = paginator.stream();
		getter.onCompletion().invoke(() -> {

			paginator.hasNextPage().subscribe().with(hasNext -> {

				if (hasNext) {

					this.subscribeComponentIntoConnections(entity, channels, paginator.nextPage());

				}
				// else finished nothing to do more

			}, error -> {

				Log.errorv(error,
						"Error when paginate the topology component that the component maybe has to be subscribed.");

			});

		}).subscribe().with(connection -> {

			this.checkSubscribeNewComponent(entity, channels, connection);

		}, error -> {

			Log.errorv(error, "Error when checking if is necessary a connection.");

		});

	}

	/**
	 * Check if must subscribe a new component into a connection.
	 *
	 * @param entity     of the new component.
	 * @param channels   to check if has to subscribe the component.
	 * @param connection to be subscribe the component.
	 */
	private void checkSubscribeNewComponent(ComponentEntity entity, List<ChannelSchema> channels,
			TopologyConnectionEntity connection) {

		final Uni<ComponentEntity> findSource = ComponentEntity.findById(connection.source.componentId);
		findSource.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the source component.");
			return null;

		}).subscribe().with(source -> {

			if (source != null) {

				for (final var sourceChannel : source.channels) {

					if (sourceChannel.name.equals(connection.source.channelName) && sourceChannel.publish != null) {

						final var sentSchema = SentMessagePayload
								.createSentMessagePayloadSchemaFor(sourceChannel.publish);
						for (final var channel : channels) {

							if (sentSchema.match(channel.subscribe, new HashMap<>())) {

								final var newNotification = new TopologyConnectionNotification();
								newNotification.node = new TopologyNode();
								newNotification.node.componentId = entity.id;
								newNotification.node.channelName = channel.name;
								newNotification.enabled = true;

								UpsertNotificationToTopologyConnection.fresh().withConnection(connection.id)
										.withNotification(newNotification).execute().subscribe().with(success -> {

											if (success) {

												AddLog.fresh().withInfo().withMessage(
														"Added notification to the channel {0} of the component {1} into the connection {2}.",
														channel.name, entity.id, connection.id).store();

											} else {

												AddLog.fresh().withError().withMessage(
														"Could not notify the channel {0} of the component {1} into the connection {2}.",
														channel.name, entity.id, connection.id).store();
											}
										});
							}
						}
						break;
					}
				}
			}

		});

	}

	/**
	 * Notify the component that is has been registered.
	 *
	 * @param entity  of the component that has been registered.
	 * @param channel that has to be notified that the component is registered.
	 */
	private void notifyComponentRegistered(ComponentEntity entity, ChannelSchema channel) {

		// found channel to notify the registered component.
		final var payload = new ComponentPayload();
		payload.id = entity.id;
		payload.name = entity.name;
		payload.description = entity.description;
		payload.version = entity.version;
		payload.apiVersion = entity.apiVersion;
		payload.type = entity.type;
		payload.since = entity.since;
		payload.channels = entity.channels;
		this.publish.send(channel.name, payload).subscribe().with(done -> {

			AddLog.fresh().withInfo().withMessage("Notified to the component {0} at {1} that it has been registered.",
					entity.id, channel.name).withPayload(payload).store();

		}, error -> {

			AddLog.fresh().withError(error)
					.withMessage("Cannot notify to the component {0} at {1} that it has been registered.", entity.id,
							channel.name)
					.withPayload(payload).store();
		});

	}

	/**
	 * Create the connection with the source.
	 *
	 * @param source           component to check to create the connection.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 */
	private void createConnectionsFor(ComponentEntity source, Collection<String> channelsToIgnore) {

		final var query = Filters.and(Filters.ne("_id", source.id),
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
				Filters.exists("channels", true), Filters.ne("channels", null),
				Filters.not(Filters.size("channels", 0)));
		final Multi<ComponentEntity> findComponents = ComponentEntity.find(query, Sorts.ascending("_id")).stream();

		findComponents.onCompletion().invoke(() -> this.createLoopConnections(source, channelsToIgnore)).subscribe()
				.with(target -> this.createConnections(source, target, channelsToIgnore),
						error -> Log.errorv(error, "Error when creating the connections."));

	}

	/**
	 * Create the loop connection of the new component.
	 *
	 * @param source           node of the connection.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 */
	private void createLoopConnections(ComponentEntity source, Collection<String> channelsToIgnore) {

		for (final var sourceChannel : source.channels) {

			if (!channelsToIgnore.contains(sourceChannel.name)) {

				for (final var targetChannel : source.channels) {

					if (!channelsToIgnore.contains(sourceChannel.name)) {

						this.createConnections(source, sourceChannel, source, targetChannel);
						if (!targetChannel.name.equals(sourceChannel.name)) {

							this.createConnections(source, targetChannel, source, sourceChannel);
						}
					}

				}
			}
		}

	}

	/**
	 * Create the connection between two components.
	 *
	 * @param source           node of the connection.
	 * @param target           node of the connection.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 */
	private void createConnections(ComponentEntity source, ComponentEntity target,
			Collection<String> channelsToIgnore) {

		for (final var sourceChannel : source.channels) {

			if (!channelsToIgnore.contains(sourceChannel.name)) {

				for (final var targetChannel : target.channels) {

					this.createConnections(source, sourceChannel, target, targetChannel);
					this.createConnections(target, targetChannel, source, sourceChannel);

				}
			}
		}

	}

	/**
	 * Check if the channels match to create a connection.
	 *
	 * @param source        node of the connection.
	 * @param sourceChannel to check if needs connection.
	 * @param target        node of the connection.
	 * @param targetChannel to check if needs connection.
	 */
	private void createConnections(ComponentEntity source, ChannelSchema sourceChannel, ComponentEntity target,
			ChannelSchema targetChannel) {

		if (sourceChannel.publish != null && sourceChannel.publish.match(targetChannel.subscribe, new HashMap<>())) {

			final var payload = new CreateConnectionPayload();
			payload.enabled = source.type != target.type;
			payload.source = new NodePayload();
			payload.source.componentId = source.id;
			payload.source.channelName = sourceChannel.name;
			payload.target = new NodePayload();
			payload.target.componentId = target.id;
			payload.target.channelName = targetChannel.name;
			Uni.createFrom().completionStage(this.create.send(payload)).subscribe()
					.with(any -> Log.debugv("Sent create the connection between {0} and {1}", sourceChannel.name,
							targetChannel.name),
							error -> Log.errorv(error, "Cannot create the connection between {0} and {1}",
									sourceChannel.name, targetChannel.name));
		}

	}

}
