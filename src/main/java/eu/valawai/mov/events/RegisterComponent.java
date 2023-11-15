/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the registration of a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ApplicationScoped
public class RegisterComponent {

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
	@Incoming("register")
	public void consume(JsonObject content) {

		final var payload = this.service.decodeAndVerify(content, RegisterComponentPayload.class);
		if (payload == null) {

			Log.errorv("The {0} is not a valid register component payload.", content);

		} else {

		}

	}

}
