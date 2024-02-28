/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link RegisterComponentManager}.
 *
 * @see RegisterComponentManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RegisterComponentManagerTest extends MovEventTestCase {

	/**
	 * The URL of the application.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.register_component.queue.name", defaultValue = "valawai/component/register")
	String registerCcomponentQueueName;

	/**
	 * Check that cannot register with an invalid payload.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidPayload() {

		final var payload = new RegisterComponentPayload();
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that the not register with an invalid AsyncAPI.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidAsyncAPI() {

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.asyncapiYaml += "channels:\n\tBad:\n\ttype: string";
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

//	/**
//	 * Check that the user register a component.
//	 */
//	@Test
//	public void shouldRegisterComponent() {
//
//		final var payload = new RegisterComponentPayloadTest().nextPayload();
//		final var countLogs = this.logs.count() + 1;
//		final var countComponents = this.components.count();
//		this.assertPublish("valawai/component/register", payload);
//		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
//		assertEquals(countComponents + 1, this.components.count());
//
//	}

}
