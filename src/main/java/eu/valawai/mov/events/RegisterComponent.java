/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import java.util.Map;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.yaml.snakeyaml.Yaml;

import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.logs.LogRecord;
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
	 * The component that manage the registered logs.
	 */
	@Inject
	LogRecordRepository logs;

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

			try {

				final Yaml yaml = new Yaml();
				final Map<String, Object> api = yaml.load(payload.asyncapiYaml);
				final var component = new Component();
				component.type = payload.type;
				component.name = payload.name;
				component.version = payload.version;
				component.apiVersion = (String) ((Map<String, Object>) api.get("info")).get("version");

			} catch (final Throwable error) {

				this.logs.add(LogRecord.builder().withError()
						.withMessage("Received invalid async API of the register component payload.")
						.withPayload(content).build());

			}

		}

	}

}
