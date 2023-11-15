/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.valawai.mov.api.v1.components.ComponentTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link ComponentRepository}.
 *
 * @see ComponentRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class ComponentRepositoryTest extends MovPersistenceTestCase {

	/**
	 * The repository to test.
	 */
	@Inject
	ComponentRepository repository;

	/**
	 * Should not found by name undefined component.
	 *
	 * @param name of an undefined component.
	 */
	@ParameterizedTest(name = "Should not found component with name {0}")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "undefined component name" })
	public void shouldNotFoundByNameUndefinedComponent(String name) {

		assertNull(this.repository.firstByName(name));
	}

	/**
	 * Should not add {@code null} component.
	 */
	@Test
	public void shouldNotAddNullComponent() {

		assertFalse(this.repository.add(null));
	}

	/**
	 * Should found by name.
	 */
	@Test
	public void shouldAddAndFoundByName() {

		final var component = new ComponentTest().nextModel();
		assertTrue(this.repository.add(component));
		assertEquals(component, this.repository.firstByName(component.name));
	}

}
