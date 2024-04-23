/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.logs;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.persistence.logs.AddLog;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The manager to consume the events to add a log.
 *
 * @see AddLogPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class AddLogManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Called when has to search for some components.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("add_log")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, AddLogPayload.class);
			return AddLog.fresh().withLevel(payload.level).withMessage(payload.message).withPayload(payload.payload)
					.withComponent(payload.componentId).execute().subscribeAsCompletionStage().thenCompose(done -> {

						if (done != null && done) {

							return msg.ack();

						} else {

							return msg.nack(new IllegalArgumentException("Cannot add the log"));
						}
					});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid add log payload.").withPayload(content).store();
			return msg.nack(error);
		}
	}
}
