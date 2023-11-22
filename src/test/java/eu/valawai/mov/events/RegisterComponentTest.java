/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.persistence.ComponentRepository;
import eu.valawai.mov.persistence.LogRecordRepository;
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
	 * The repository with the logs.
	 */
	@Inject
	LogRecordRepository logs;

	/**
	 * Check that the user register a component.
	 */
	@Test
	public void shouldRegisterComponent() {

		final var payload = new RegisterComponentPayloadTest().nextPayload();
		this.assertPublish("valawai/component/register", payload);

	}

	/**
	 * Check that the not register a bad component.
	 */
	@Test
	public void shouldNotRegisterComponent() {

		final var countComponents = this.components.count();
		final var countLogs = this.logs.count();
		final var payload = new RegisterComponentPayload();
		payload.name = nextUUID().toString();
		this.assertPublish("valawai/component/register", payload);
		assertEquals(countComponents, this.components.count());
		assertEquals(countLogs + 1, this.logs.count());
		final var log = this.logs.last();
		assertEquals(LogLevel.ERROR, log.level);
		assertEquals(JsonObject.mapFrom(payload), log.payload);

	}

}
