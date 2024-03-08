/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.events.PublishService;
import eu.valawai.mov.events.topology.ChangeTopologyPayload;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.components.AddComponent;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.AddLog;
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
	 * Notify that a new connection is created.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.change_topology.queue.name", defaultValue = "valawai/topology/change")
	String changeTopologyQueueName;

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

						AddLog.fresh().withInfo().withMessage("Added the component {0}.", source).withPayload(payload)
								.store();
						final var paginator = ComponentEntity.find("channels != null", Sort.ascending("_id"))
								.page(Page.ofSize(10));
						this.createConnections(source, paginator);
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
	 * Create the connection with the source and the components of a page.
	 *
	 * @param source    component to check to create the connection.
	 * @param paginator function that paginate the components.
	 */
	private void createConnections(ComponentEntity source,
			ReactivePanacheQuery<ReactivePanacheMongoEntityBase> paginator) {

		paginator.hasNextPage().subscribe().with(hasNext -> {

			if (hasNext) {

				final Multi<ComponentEntity> pageGetter = paginator.nextPage().stream();
				pageGetter.onCompletion().invoke(() -> {

					this.createConnections(source, paginator);

				}).subscribe().with(target -> {

					this.createConnections(source, target);

				}, error -> {

					Log.errorv(error, "Error when checking if is necessary a connection.");

				});
			}

		}, error -> {

			Log.errorv(error, "Error when paginate the component to create connections.");

		});

	}

	/**
	 * Create the connection between two components.
	 *
	 * @param source node of the connection.
	 * @param target node of the connection.
	 */
	private void createConnections(ComponentEntity source, ComponentEntity target) {

		if (source.channels != null && target.channels != null) {

			for (final var sourceChannel : source.channels) {

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

		if (sourceChannel.publish != null && sourceChannel.publish.match(targetChannel.subscribe)) {

			AddTopologyConnection.fresh().withSourceComponent(source.id).withSourceChannel(sourceChannel.id)
					.withTargetComponent(target.id).withTargetChannel(targetChannel.id).execute().subscribe()
					.with(connectionId -> {

						if (connectionId != null) {

							AddLog.fresh().withInfo().withMessage("Added connection between {0} and {1}",
									sourceChannel.id, targetChannel.id).store();
							final var msg = new ChangeTopologyPayload();
							msg.action = TopologyAction.ENABLE;
							msg.connectionId = connectionId;
							this.publish.send(this.changeTopologyQueueName, msg).subscribe().with(done -> {

								Log.debugv("Sent enable the connection between {0} and {1}", sourceChannel.id,
										targetChannel.id);

							}, error -> {

								Log.errorv(error, "Cannot enable the connection between {0} and {1}", sourceChannel.id,
										targetChannel.id);
							});

						} else {

							Log.errorv("Cannot create connection between {0} and {1}", sourceChannel.id,
									targetChannel.id);
						}

					});
		}

	}

}
