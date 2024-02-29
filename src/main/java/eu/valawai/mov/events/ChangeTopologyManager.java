/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import eu.valawai.mov.persistence.logs.AddLog;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the changes on the topology.
 *
 * @see ChangeTopologyPayload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ChangeTopologyManager {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	PublishService publish;

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	ListenerService listener;

	/**
	 * Called when has to register a component.
	 *
	 * @param content of the message to consume.
	 */
	@Incoming("change_topology")
	public void consume(JsonObject content) {

		final var payload = this.service.decodeAndVerify(content, ChangeTopologyPayload.class);
		if (payload == null) {

			AddLog.fresh().withError().withMessage("Received invalid change topology payload.").withPayload(content)
					.store();

		} else {
			// do something
			this.listener.open(payload.source).subscribe().with(msg -> this.publish.send(payload.target, msg));
		}

	}

}
