/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.services.LocalConfigService;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;

/**
 * Generic class to test the {@link RegisterComponentManager}.
 *
 * @see RegisterComponentManager
 *
 * @author VALAWAI
 */
public class RegisterComponentManagerTestCase extends MovEventTestCase {

	/**
	 * The name of the queue to send the register component events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.register_component.queue.name", defaultValue = "valawai/component/register")
	String registerComponentQueueName;

	/**
	 * The local configuration.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * Check that can not register a component.
	 *
	 * @param payload with the information of the component that can not be
	 *                registered.
	 */
	protected void assertNotRegister(RegisterComponentPayload payload) {

		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerComponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));

	}

	/**
	 * Check that is load an specification resource.
	 *
	 * @param name of the resource to load.
	 *
	 * @return the string of the loaded resource template.
	 */
	protected String loadResourceContent(String name) {

		try {

			final var loader = this.getClass().getClassLoader();
			final var stream = loader.getResourceAsStream("eu/valawai/mov/events/components/" + name);
			final var bytes = stream.readAllBytes();
			return new String(bytes, StandardCharsets.UTF_8);

		} catch (final Throwable error) {

			fail(error.getMessage());
			return null;
		}

	}

}
