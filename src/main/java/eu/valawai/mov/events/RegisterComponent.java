/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.persistence.components.AddComponent;
import eu.valawai.mov.persistence.logs.AddLog;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The element used to manage the registration of a component.
 *
 * @author VALAWAI
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

			AddLog.fresh().withError().withMessage("Received invalid register component payload.").withPayload(content)
					.store();

		} else {

			final var component = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
			if (component == null) {

				AddLog.fresh().withError().withMessage("Received invalid async API.").withPayload(content).store();

			} else {

				component.type = payload.type;
				if (payload.name != null) {

					component.name = payload.name;
				}
				if (payload.version != null) {

					component.version = payload.version;
				}
				AddComponent.fresh().withComponent(component).execute().subscribe().with(result -> {

					if (result) {

						AddLog.fresh().withInfo().withMessage("Added component.").withPayload(component).store();

					} else {

						Log.errorv("Cannot store the component {0}", component);
					}

				});
			}
		}

	}

}
