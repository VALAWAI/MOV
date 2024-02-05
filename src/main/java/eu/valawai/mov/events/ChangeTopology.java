/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import eu.valawai.mov.api.v1.logs.LogRecord;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the topology management.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ChangeTopology {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Called when has to register a component.
	 *
	 * @param content of the message to consume.
	 */
	@Incoming("change_topology")
	public void consume(JsonObject content) {

		final var payload = this.service.decodeAndVerify(content, ChangeTopologyComponentPayload.class);
		if (payload == null) {

			LogRecord.builder().withError().withMessage("Received invalid change topology payload.")
					.withPayload(content).store();

		} else {
			// do something
		}

	}

}
