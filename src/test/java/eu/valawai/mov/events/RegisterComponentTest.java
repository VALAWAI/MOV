/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.persistence.ComponentRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link RegisterComponent}.
 *
 * @see RegisterComponent
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class RegisterComponentTest extends MovEventTestCase {

	/**
	 * The repository with the components.
	 */
	@Inject
	ComponentRepository components;

	/**
	 * Check that cannot register with an invalid payload.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidPayload() {

		final var payload = new RegisterComponentPayload();
		final var countLogs = this.logs.count() + 1;
		final var countComponents = this.components.count();
		this.assertPublish("valawai/component/register", payload);
		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
		assertEquals(countComponents, this.components.count());

		final var log = this.logs.last();
		assertEquals(LogLevel.ERROR, log.level);
		assertEquals(JsonObject.mapFrom(payload).encodePrettily(), log.payload);

	}

	/**
	 * Check that the not register with an invalid AsyncAPI.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidAsyncAPI() {

		final var payload = new RegisterComponentPayloadTest().nextPayload();
		payload.asyncapiYaml += "channels:\n\tBad:\n\ttype: string";
		final var countLogs = this.logs.count() + 1;
		final var countComponents = this.components.count();
		this.assertPublish("valawai/component/register", payload);
		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
		assertEquals(countComponents, this.components.count());

		final var log = this.logs.last();
		assertEquals(LogLevel.ERROR, log.level);
		assertEquals(JsonObject.mapFrom(payload).encodePrettily(), log.payload);

	}

	/**
	 * Check that the user register a component.
	 */
	@Test
	public void shouldRegisterComponent() {

		final var payload = new RegisterComponentPayloadTest().nextPayload();
		final var countLogs = this.logs.count() + 1;
		final var countComponents = this.components.count();
		this.assertPublish("valawai/component/register", payload);
		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
		assertEquals(countComponents + 1, this.components.count());

	}

}
