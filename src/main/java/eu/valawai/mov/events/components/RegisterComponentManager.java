/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.CreateConnectionPayload;
import eu.valawai.mov.events.topology.NodePayload;
import eu.valawai.mov.persistence.components.AddComponent;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.AddLog;
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

			final var payload = this.service.decodeAndVerify(content, RegisterComponentPayload.class);
			final var component = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
			if (component == null) {

				AddLog.fresh().withError().withMessage("Received invalid async API.").withPayload(content).store();
				return msg.nack(new IllegalArgumentException("The async API is not valid."));

			} else {

				component.type = payload.type;
				if (payload.name != null) {

					component.name = payload.name;
				}
				if (payload.version != null) {

					component.version = payload.version;
				}
				final Uni<Throwable> add = AddComponent.fresh().withComponent(component).execute().map(source -> {

					if (source != null) {

						AddLog.fresh().withInfo().withMessage("Added the component {0}.", source.id)
								.withPayload(payload).store();
						final var channelsToIgnore = new HashSet<String>();
						this.notifyComponentRegistered(source, channelsToIgnore);

						final var paginator = ComponentEntity.find("channels != null", Sort.ascending("_id"))
								.page(Page.ofSize(10));
						this.createConnections(source, paginator, channelsToIgnore);
						return null;

					} else {

						Log.errorv("Cannot store the component {0}", component);
						return new IllegalStateException("Cannot store the component");

					}

				});
				return add.subscribeAsCompletionStage().thenCompose(error -> {

					if (error == null) {

						return msg.ack();

					} else {

						return msg.nack(error);
					}
				});
			}

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid register component payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}

	/**
	 * Notify the component that it is has been registered.
	 *
	 * @param entity           of the component that has been registered.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 */
	private void notifyComponentRegistered(ComponentEntity entity, Collection<String> channelsToIgnore) {

		if (entity.channels != null) {

			final var expectedNamePattern = "valawai/" + entity.type.name().toLowerCase() + "/\\w+/control/registered";

			for (final var channel : entity.channels) {

				if (channel.subscribe instanceof final ObjectPayloadSchema schema
						&& channel.name.matches(expectedNamePattern) && schema.properties.containsKey("id")
						&& schema.properties.containsKey("name") && schema.properties.containsKey("description")
						&& schema.properties.containsKey("version") && schema.properties.containsKey("api_version")
						&& schema.properties.containsKey("type") && schema.properties.containsKey("since")
						&& schema.properties.containsKey("channels")) {
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

						AddLog.fresh().withInfo()
								.withMessage("Notified to the component {0} at {1} that it has been registered.",
										entity.id, channel.name)
								.withPayload(payload).store();

					}, error -> {

						AddLog.fresh().withError(error)
								.withMessage("Cannot notify to the component {0} at {1} that it has been registered.",
										entity.id, channel.name)
								.withPayload(payload).store();
					});
					channelsToIgnore.add(channel.name);
					return;
				}

			}
		}

	}

	/**
	 * Create the connection with the source and the components of a page.
	 *
	 * @param source           component to check to create the connection.
	 * @param paginator        function that paginate the components.
	 * @param channelsToIgnore collection to add the channels to ignore.
	 */
	private void createConnections(ComponentEntity source,
			ReactivePanacheQuery<ReactivePanacheMongoEntityBase> paginator, Collection<String> channelsToIgnore) {

		final Multi<ComponentEntity> getter = paginator.stream();
		getter.onCompletion().invoke(() -> {

			paginator.hasNextPage().subscribe().with(hasNext -> {

				if (hasNext) {

					this.createConnections(source, paginator.nextPage(), channelsToIgnore);

				}
				// else finished nothing to do

			}, error -> {

				Log.errorv(error, "Error when paginate the component to create connections.");

			});

		}).subscribe().with(target -> {

			this.createConnections(source, target, channelsToIgnore);

		}, error -> {

			Log.errorv(error, "Error when checking if is necessary a connection.");

		});

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

		if (source.channels != null && target.channels != null) {

			for (final var sourceChannel : source.channels) {

				if (!channelsToIgnore.contains(sourceChannel.name)) {

					for (final var targetChannel : target.channels) {

						if (!channelsToIgnore.contains(targetChannel.name)) {

							this.createConnections(source, sourceChannel, target, targetChannel);
							this.createConnections(target, targetChannel, source, sourceChannel);
						}

					}

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

		if (sourceChannel.publish != null && sourceChannel.publish.match(targetChannel.subscribe)) {

			final var payload = new CreateConnectionPayload();
			payload.enabled = source.type != target.type;
			payload.source = new NodePayload();
			payload.source.componentId = source.id;
			payload.source.channelName = sourceChannel.name;
			payload.target = new NodePayload();
			payload.target.componentId = target.id;
			payload.target.channelName = targetChannel.name;
			this.create.send(payload).handle((success, error) -> {

				if (error == null) {

					Log.debugv("Sent create the connection between {0} and {1}", sourceChannel.name,
							targetChannel.name);

				} else {

					Log.errorv(error, "Cannot create the connection between {0} and {1}", sourceChannel.name,
							targetChannel.name);

				}
				return null;
			});
		}

	}

}
