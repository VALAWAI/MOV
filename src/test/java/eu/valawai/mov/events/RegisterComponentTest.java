/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.persistence.ComponentRepository;
import io.quarkus.test.junit.QuarkusTest;
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
	 * The repository to test.
	 */
	@Inject
	ComponentRepository repository;

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
		assertNull(this.repository.firstByName(payload.name));

	}

}
