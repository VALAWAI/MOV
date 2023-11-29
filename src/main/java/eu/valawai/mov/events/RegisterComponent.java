/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.logs.LogRecord;
import eu.valawai.mov.persistence.ComponentRepository;
import eu.valawai.mov.persistence.LogRecordRepository;
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
	 * The service that manage the registered logs.
	 */
	@Inject
	LogRecordRepository logs;

	/**
	 * The service that manage the registered components.
	 */
	@Inject
	ComponentRepository components;

	/**
	 * Called when has to register a component.
	 *
	 * @param content of the message to consume.
	 */
	@Incoming("register")
	public void consume(JsonObject content) {

		final var payload = this.service.decodeAndVerify(content, RegisterComponentPayload.class);
		if (payload == null) {

			this.logs.add(LogRecord.builder().withError().withMessage("Received invalid register component payload.")
					.withPayload(content).build());

		} else {

			final var component = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
			if (component == null) {

				this.logs.add(LogRecord.builder().withError().withMessage("Received invalid async API.")
						.withPayload(content).build());

			} else {

				component.type = payload.type;
				component.name = payload.name;
				component.version = payload.version;
				if (!this.components.add(component)) {
					// It never happens in theory
					this.logs.add(LogRecord.builder().withError().withMessage("Cannot store the component to register.")
							.withPayload(content).build());

				} else {

					this.logs.add(LogRecord.builder().withInfo().withMessage("Registered the component {0}", component)
							.withPayload(content).build());

				}
			}
		}

	}

}
