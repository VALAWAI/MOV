/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.bson.types.ObjectId;
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
import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.api.v2.design.topologies.TopologyConnection;
import eu.valawai.mov.api.v2.design.topologies.TopologyConnectionEndpoint;
import eu.valawai.mov.api.v2.design.topologies.TopologyConnectionNotification;
import eu.valawai.mov.api.v2.design.topologies.TopologyNode;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.CreateConnectionPayload;
import eu.valawai.mov.events.topology.CreateNotificationPayload;
import eu.valawai.mov.events.topology.NodePayload;
import eu.valawai.mov.events.topology.SentMessagePayload;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.services.LocalConfigService;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.FindOptions;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the registration of a component.
 *
 * @see RegisterComponentPayload
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
	Emitter<CreateConnectionPayload> createConnection;

	/**
	 * The service to send messages.
	 */
	@Inject
	PublishService publishRegistered;

	/**
	 * The local configuration.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * The component to send the message to create a topology notification.
	 */
	@Inject
	@Channel("send_create_notification")
	Emitter<CreateNotificationPayload> createNotification;

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

					AddLog.fresh().withInfo().withMessage("Registered component {0}.", entity.id).withPayload(content)
							.store();
					context.entity = entity;
					switch (context.behaviour) {
					case TopologyBehavior.AUTO_DISCOVER:
						this.autoDiscover(context);
						break;
					case TopologyBehavior.APPLY_TOPOLOGY:
						this.createTopologyConnections(context);
						break;
					case TopologyBehavior.APPLY_TOPOLOGY_OR_AUTO_DISCOVER:
						if (context.definition == null) {

							this.autoDiscover(context);

						} else {

							this.createTopologyConnections(context);
						}
						break;
					default:
						// DO_NOTHING
					}

					this.notifyComponentRegistered(context);
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

		} else {

			if (context.component.channels != null) {

				final var expectedNamePattern = "valawai/" + context.component.type.name().toLowerCase()
						+ "/\\w+/control/\\w+";
				for (final var channel : context.component.channels) {

					if (channel.subscribe instanceof final ObjectPayloadSchema schema
							&& channel.name.matches(expectedNamePattern) && channel.name.endsWith("/registered")
							&& schema.properties.keySet().containsAll(Arrays.asList(REGISTERED_PAYLOAD_FIELD_NAMES))) {

						context.notifyRegisteredChannel = channel;

					} else if (context.component.type == ComponentType.C2
							&& channel.subscribe instanceof final ObjectPayloadSchema schema && schema.properties
									.keySet().containsAll(Arrays.asList(SENT_MESSAGE_PAYLOAD_FIELD_NAMES))) {

						context.notifyChannels.add(channel);

					} else {

						context.posibleConnectionChannels.add(channel);
					}
				}
			}

			if (context.behaviour == TopologyBehavior.APPLY_TOPOLOGY
					|| context.behaviour == TopologyBehavior.APPLY_TOPOLOGY_OR_AUTO_DISCOVER) {

				return this.validateTopology(context);

			} else {

				return Uni.createFrom().nullItem();

			}

		}
	}

	/**
	 * Check that the component follow the topology.
	 *
	 * @param context with the payload to validate.
	 *
	 * @return null of the component follows the topology or the error that explains
	 *         why is not valid.
	 */
	private Uni<Throwable> validateTopology(ManagerContext context) {

		return this.configuration.getTopology().chain(topology -> {

			if (topology == null && context.behaviour != TopologyBehavior.APPLY_TOPOLOGY_OR_AUTO_DISCOVER) {

				return Uni.createFrom().failure(new IllegalStateException("The topology to follow is not defined."));

			} else if (topology != null) {

				context.topology = topology;
				if (topology.nodes != null) {

					for (final var node : topology.nodes) {

						if (node.component != null) {

							if (node.component.channels == null || node.component.channels.isEmpty()) {

								if (context.component.channels == null || context.component.channels.isEmpty()) {

									context.definition = node;
									break;
								}

							} else if (context.component.channels != null
									&& this.match(node.component.channels, context.component.channels)) {

								context.definition = node;
								break;
							}

						} // else bad defined node
					}
				}

				if (context.definition == null && context.behaviour == TopologyBehavior.APPLY_TOPOLOGY) {

					return Uni.createFrom().failure(new IllegalStateException(
							"The topology does not contains a definition to match the component."));

				}

			}
			return Uni.createFrom().nullItem();

		});

	}

	/**
	 * Context used by this manager.
	 */
	private class ManagerContext {

		/**
		 * The channels that can be used to create a connection.
		 */
		public List<ChannelSchema> posibleConnectionChannels = new ArrayList<>();

		/**
		 * The channels to be notified when a message pass thought a connection.
		 */
		public List<ChannelSchema> notifyChannels = new ArrayList<>();

		/**
		 * The channel to notify when the component is registered.
		 */
		public ChannelSchema notifyRegisteredChannel;

		/**
		 * The behaviour to do after the component has been registered.
		 */
		public TopologyBehavior behaviour;

		/**
		 * The component to be registered.
		 */
		public Component component;

		/**
		 * The definition of the component to be registered.
		 */
		public TopologyNode definition;

		/**
		 * The topology to follow.
		 */
		public Topology topology;

		/**
		 * The stored component.
		 */
		public ComponentEntity entity;

		/**
		 * Create a new context
		 *
		 * @param payload with the connection to be created.
		 */
		public ManagerContext(RegisterComponentPayload payload) {

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

		/**
		 * Check if the definition match the specified endpoint.
		 *
		 * @param endpoint to check if match.
		 *
		 * @return {@code true} if the defined node for the component match the
		 *         endpoint.
		 */
		public boolean definitionMatch(TopologyConnectionEndpoint endpoint) {

			if (endpoint == null || endpoint.nodeTag == null || endpoint.channel == null || this.definition == null) {

				return false;

			} else {

				return this.definition.tag.equals(endpoint.nodeTag);
			}
		}

	}

	/**
	 * Auto discover connections.
	 *
	 * @param context of the register process.
	 */
	private void autoDiscover(ManagerContext context) {

		// Auto discover connections
		if (!context.posibleConnectionChannels.isEmpty()) {

			final var query = Filters.and(Filters.ne("_id", context.entity.id),
					Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
					Filters.exists("channels", true), Filters.ne("channels", null),
					Filters.not(Filters.size("channels", 0)));
			final Multi<ComponentEntity> findComponents = ComponentEntity.find(query, Sorts.ascending("_id")).stream();
			findComponents.subscribe().with(target -> {

				if (target != null && target.channels != null) {

					for (final var sourceChannel : context.posibleConnectionChannels) {

						for (final var targetChannel : target.channels) {

							this.createConnection(context.entity, sourceChannel, target, targetChannel);
							this.createConnection(target, targetChannel, context.entity, sourceChannel);
						}
					}
				}

			}, error -> Log.errorv(error, "Error when creating the connections."));

			// Create the loop connections
			for (final var sourceChannel : context.posibleConnectionChannels) {

				for (final var targetChannel : context.posibleConnectionChannels) {

					this.createConnection(context.entity, sourceChannel, context.entity, targetChannel);
				}
			}

		}

		// Auto discover notifications
		if (!context.notifyChannels.isEmpty()) {

			final Multi<TopologyConnectionEntity> findComponents = TopologyConnectionEntity
					.find("deletedTimestamp is null and notifications.node.componentId != ?1", Sorts.ascending("_id"),
							context.entity.id)
					.stream();
			findComponents.subscribe().with(target -> this.autoDiscoverNotificationForConnection(target, context),
					error -> Log.errorv(error, "Error when creating the notifications."));

		}

	}

	/**
	 * Auto discover the possible notification for a connection.
	 *
	 * @param connection to check if exist possible notifications.
	 * @param context    used in the register process.
	 */
	private void autoDiscoverNotificationForConnection(TopologyConnectionEntity connection, ManagerContext context) {

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
						for (final var channel : context.notifyChannels) {

							if (sentSchema.match(channel.subscribe, new HashMap<>())) {

								final var payload = new CreateNotificationPayload();
								payload.connectionId = connection.id;
								payload.target = new NodePayload();
								payload.target.componentId = context.entity.id;
								payload.target.channelName = channel.name;
								Uni.createFrom().completionStage(this.createNotification.send(payload)).subscribe()
										.with(any -> Log.debugv("Sent create a notification between {0}", payload),
												error -> Log.errorv(error, "Cannot create the notification {0}",
														payload));
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
	 * @param context with the information of the register process.
	 */
	private void notifyComponentRegistered(ManagerContext context) {

		if (context.notifyRegisteredChannel != null) {

			// found channel to notify the registered component.
			final var payload = new ComponentPayload();
			payload.id = context.entity.id;
			payload.name = context.entity.name;
			payload.description = context.entity.description;
			payload.version = context.entity.version;
			payload.apiVersion = context.entity.apiVersion;
			payload.type = context.entity.type;
			payload.since = context.entity.since;
			payload.channels = context.entity.channels;
			this.publishRegistered.send(context.notifyRegisteredChannel.name, payload).subscribe().with(done -> {

				AddLog.fresh().withInfo()
						.withMessage("Notified to the component {0} at {1} that it has been registered.",
								context.entity.id, context.notifyRegisteredChannel.name)
						.withPayload(payload).store();

			}, error -> {

				AddLog.fresh().withError(error)
						.withMessage("Cannot notify to the component {0} at {1} that it has been registered.",
								context.entity.id, context.notifyRegisteredChannel.name)
						.withPayload(payload).store();
			});
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
	private void createConnection(ComponentEntity source, ChannelSchema sourceChannel, ComponentEntity target,
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
			this.send(payload);
		}

	}

	/**
	 * Send the message to create a connection.
	 *
	 * @param payload with the connection to create.
	 */
	private void send(CreateConnectionPayload payload) {

		Uni.createFrom().completionStage(this.createConnection.send(payload)).subscribe().with(
				any -> Log.debugv("Sent  the {0}", payload),
				error -> Log.errorv(error, "Cannot send the {0}", payload));

	}

	/**
	 * Create the connections associated to the new component.
	 *
	 * @param context of the register process.
	 */
	private void createTopologyConnections(ManagerContext context) {

		if (context.topology.connections != null) {

			for (final var connection : context.topology.connections) {

				if (context.definitionMatch(connection.source)) {

					if (context.definitionMatch(connection.target)) {
						// loop
						this.sendCreateConnection(connection, context.entity.id, context.entity.id);

					} else {

						this.searchComponent(connection.target, context,
								target -> this.sendCreateConnection(connection, context.entity.id, target.id));
					}

				} else if (context.definitionMatch(connection.target)) {

					this.searchComponent(connection.source, context,
							source -> this.sendCreateConnection(connection, source.id, context.entity.id));

				} else if (connection.notifications != null) {

					for (final var notification : connection.notifications) {

						if (context.definitionMatch(notification.target)) {

							this.searchMathingConnectionWith(notification, context, connection);
						}
					}

				}
			}
		}

	}

	/**
	 * Send the message to create the connection between the specified components.
	 *
	 * @param connection to create.
	 * @param sourceId   identifier of the source component.
	 * @param targetId   identifier of the target component
	 */
	private void sendCreateConnection(TopologyConnection connection, ObjectId sourceId, ObjectId targetId) {

		TopologyConnectionEntity.count("""
						source.componentId = ?1 and source.channelName = ?2
						and target.componentId = ?3 and target.channelName = ?4
				""", sourceId, connection.source.channel, targetId, connection.target.channel).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot count the connections.");
					return 1l;

				}).subscribe().with(count -> {

					if (count == 0l) {

						final var payload = new CreateConnectionPayload();
						payload.enabled = true;
						payload.source = new NodePayload();
						payload.source.componentId = sourceId;
						payload.source.channelName = connection.source.channel;
						payload.target = new NodePayload();
						payload.target.componentId = targetId;
						payload.target.channelName = connection.target.channel;
						payload.converterJSCode = connection.convertCode;
						this.send(payload);

					}

				});

	}

	/**
	 * Check if the defined channels match the channels of a component.
	 *
	 * @param source to check if match the other channels.
	 * @param target to check if match the defined.
	 *
	 * @return {@code true} if the defined channels match the registered channels.
	 */
	private boolean match(List<ChannelSchema> source, List<ChannelSchema> target) {

		int matches = 0;
		for (final var defined : source) {

			for (final var registered : target) {

				if (defined.match(registered)) {

					matches++;
					break;
				}
			}

		}

		return matches == source.size();

	}

	/**
	 * Search for the component that match the specified defined node.
	 *
	 * @param context  of the register process.
	 * @param endpoint to get the component that match it.
	 */
	private void searchComponent(TopologyConnectionEndpoint endpoint, ManagerContext context,
			Consumer<ComponentEntity> found) {

		for (final var node : context.topology.nodes) {

			if (node.tag.equals(endpoint.nodeTag) && node.component != null && node.component.channels != null) {

				final var channelByName = this.channelByName(node.component.channels, endpoint.channel);
				if (channelByName != null) {

					final var filter = Filters.and(Filters.eq("type", node.component.type),
							Filters.exists("channels", true), Filters.ne("channels", null),
							Filters.eq("channels.name", endpoint.channel),
							Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)));
					final var options = new FindOptions().sort(Sorts.ascending("_id")).limit(1);
					ComponentEntity.mongoCollection().find(filter, ComponentEntity.class, options).select()
							.where(component -> {

								final var componentChannel = this.channelByName(component.channels, channelByName.name);
								return channelByName.match(componentChannel);

							}, 1).collect().first().onFailure().recoverWithItem(error -> {

								Log.errorv(error, "Cannot find the component");
								return null;

							}).subscribe().with(component -> {

								if (component != null) {

									found.accept(component);
								}

							});
					break;
				}
			}
		}
	}

	/**
	 * Check if two channels match.
	 */

	/**
	 * Return the channel that has the specified name.
	 *
	 * @param channels to search.
	 * @param name     of the channel to search.
	 *
	 * @return the channel that has the specified name, or {@code null} if not
	 *         found.
	 */
	private ChannelSchema channelByName(List<ChannelSchema> channels, String name) {

		for (final var channel : channels) {

			if (channel.name.equals(name)) {

				return channel;
			}
		}
		return null;
	}

	/**
	 * Return the channel definition of the specified endpoint.
	 *
	 * @param endpoint to get the channel.
	 * @param context  of the register process.
	 *
	 * @return the channel associated to the endpoint, or {@code null} if not found.
	 */
	private ChannelSchema channelOf(TopologyConnectionEndpoint endpoint, ManagerContext context) {

		for (final var node : context.topology.nodes) {

			if (node.tag.equals(endpoint.nodeTag) && node.component != null && node.component.channels != null) {

				return this.channelByName(node.component.channels, endpoint.channel);
			}
		}

		return null;
	}

	/**
	 * Send the message to create a notification for a connection.
	 *
	 * @param notification to create.
	 * @param connectionId identifier of the connection to add the notification.
	 * @param targetId     identifier of the target component
	 */
	private void sendCreateNotification(TopologyConnectionNotification notification, ObjectId connectionId,
			ObjectId targetId) {

		final var payload = new CreateNotificationPayload();
		payload.enabled = true;
		payload.connectionId = connectionId;
		payload.target = new NodePayload();
		payload.target.componentId = targetId;
		payload.target.channelName = notification.target.channel;
		payload.converterJSCode = notification.convertCode;
		Uni.createFrom().completionStage(this.createNotification.send(payload)).subscribe().with(
				any -> Log.debugv("Sent  the {0}", payload),
				error -> Log.errorv(error, "Cannot send the {0}", payload));

	}

	/**
	 * Check if exist a connection that match the specified notification.
	 *
	 * @param notification to create.
	 * @param context      of the register process.
	 * @param connection   where the notification will be defined.
	 */
	public void searchMathingConnectionWith(TopologyConnectionNotification notification, ManagerContext context,
			TopologyConnection connection) {

		final var sourceNodeChannel = this.channelOf(connection.source, context);
		final var targetNodeChannel = this.channelOf(connection.target, context);
		final Multi<TopologyConnectionEntity> find = TopologyConnectionEntity
				.find("source.channelName = ?1 and target.channelName = ?2 and deletedTimestamp is null",
						connection.source.channel, connection.target.channel)
				.stream();
		find.select().when(liveConnection -> {

			return this.componentHasChannel(liveConnection.source.componentId, sourceNodeChannel).chain(hasSource -> {

				if (hasSource) {

					return this.componentHasChannel(liveConnection.target.componentId, targetNodeChannel);

				} else {

					return Uni.createFrom().item(false);
				}

			});

		}).subscribe().with(liveConnection -> {

			if (liveConnection != null) {

				this.sendCreateNotification(notification, liveConnection.id, context.entity.id);
			}

		}, error -> Log.errorv(error, "Cannot find the connection."));
	}

	/**
	 * Check if a component has the specified channel.
	 *
	 * @param componetId identifier of the component to check.
	 * @param channel    to be defined in the component.
	 *
	 * @return {@code true} if the component has the specified channel.
	 */
	private Uni<Boolean> componentHasChannel(ObjectId componetId, ChannelSchema channel) {

		final Uni<ComponentEntity> find = ComponentEntity.findById(componetId);
		return find.onFailure().recoverWithNull().onItem().transform(component -> {

			var match = false;
			if (component != null) {

				final var defined = this.channelByName(component.channels, channel.name);
				match = defined != null && channel.match(defined);
			}
			return match;

		});

	}

}
