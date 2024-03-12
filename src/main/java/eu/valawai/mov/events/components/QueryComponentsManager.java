/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import eu.valawai.mov.events.PayloadService;
import eu.valawai.mov.persistence.components.GetComponentsPagePayload;
import eu.valawai.mov.persistence.logs.AddLog;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The manager to consume the events that asks for some components.
 *
 * @see QueryComponentsPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class QueryComponentsManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * The component to publish the query result.
	 */
	@Inject
	@Channel("components_page")
	Emitter<ComponentsPagePayload> emitter;

	/**
	 * Called when has to search for some components.
	 *
	 * @param msg message to consume.
	 *
	 * @return the result if the message process.
	 */
	@Incoming("query_components")
	public CompletionStage<Void> consume(Message<JsonObject> msg) {

		final var content = msg.getPayload();
		try {

			final var payload = this.service.decodeAndVerify(content, QueryComponentsPayload.class);
			return GetComponentsPagePayload.fresh().withQuery(payload).execute().subscribeAsCompletionStage()
					.thenCompose(page -> {

						if (page != null) {

							AddLog.fresh().withInfo().withMessage("Found the page for the query {0}.", payload.id)
									.withPayload(page).store();
							this.emitter.send(page);
							return msg.ack();

						} else {

							AddLog.fresh().withError().withMessage("No page found for query {0}.", payload.id).store();
							return msg.nack(new IllegalArgumentException("Cannot get the query result"));
						}
					});

		} catch (final Throwable error) {

			AddLog.fresh().withError().withMessage("Received invalid query components payload.").withPayload(content)
					.store();
			return msg.nack(error);
		}

	}
}
