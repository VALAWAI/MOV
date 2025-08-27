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

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.ChangeNotificationFromConnection;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the changes on a notification defined in a
 * topology connection.
 *
 * @see ChangeNotificationPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ChangeNotificationManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Called when has to register a component.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("change_topology_notification")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, ChangeNotificationPayload.class);
			return ChangeNotificationFromConnection.fresh().withConnection(payload.connectionId)
					.withNode(payload.target).withAction(payload.action).execute().subscribeAsCompletionStage()
					.thenCompose(changed -> {
						if (Boolean.TRUE.equals(changed)) {

							AddLog.fresh().withDebug().withMessage("Changed notification").withPayload(payload).store();
							return msg.ack();

						} else {

							AddLog.fresh().withError()
									.withMessage("Cannot changed notification, because it is not defined.")
									.withPayload(payload).store();
							return msg.nack(new IllegalArgumentException("Cannot find the notification to change"));
						}
					});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid change notification payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}

}
