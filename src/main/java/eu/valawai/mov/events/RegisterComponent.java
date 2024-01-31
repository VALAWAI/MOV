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

			LogRecord.builder().withError().withMessage("Received invalid register component payload.")
					.withPayload(content).store();

		} else {

			final var component = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
			if (component == null) {

				LogRecord.builder().withError().withMessage("Received invalid async API.").withPayload(content).store();

			} else {

				component.type = payload.type;
				component.name = payload.name;
				component.version = payload.version;
//				final var added = this.components.add(component);
//				if (added == null) {
//					// It never happens in theory
//					LogRecord.builder().withError().withMessage("Cannot store the component to register.")
//							.withPayload(content).store();
//
//				} else {
//
//					LogRecord.builder().withInfo().withMessage("Registered the component {0}", added)
//							.withPayload(content).store();
//
//					for (final var channel : added.channels) {
//
//						if (channel.subscribe != null) {
//
//						}
//						if (channel.publish != null) {
//
//						}
//					}
//				}
			}
		}

	}

}
