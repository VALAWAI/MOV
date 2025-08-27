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
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.services.LocalConfigService;
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

			return msg.ack();

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid create topology connection payload.")
					.withPayload(content).store();
			return msg.nack(error);
		}

	}

}
