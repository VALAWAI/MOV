/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.persistence.ComponentEntity;
import io.quarkus.test.junit.QuarkusTest;

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

		final var payload = new RegisterComponentPayload();
		payload.name = nextUUID().toString();
		this.assertPublish("valawai/component/register", payload);
		final var components = ComponentEntity.find(" name = ?1", payload.name).list().await()
				.atMost(Duration.ofSeconds(30));
		assertNotNull(components);
		assertTrue(components.isEmpty());

	}

}
