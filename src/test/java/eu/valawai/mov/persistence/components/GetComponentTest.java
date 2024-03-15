/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.components.ComponentTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetComponent}.
 *
 * @see GetComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetComponentTest extends MovPersistenceTestCase {

	/**
	 * Should get {@code null} without component.
	 */
	@Test
	public void shouldGetNullComponentForNoCompnentId() {

		this.assertItemNullOrFailed(GetComponent.fresh().execute());

	}

	/**
	 * Should get {@code null} with an undefined component.
	 */
	@Test
	public void shouldGetNullComponentForUndefinedComponent() {

		this.assertExecutionNull(GetComponent.fresh().withComponent(nextObjectId()));

	}

	/**
	 * Should get a component.
	 */
	@Test
	public void shouldGetComponent() {

		final var component = ComponentEntities.nextComponent();
		final var result = this.assertExecutionNotNull(GetComponent.fresh().withComponent(component.id));
		final var expected = ComponentTest.from(component);
		assertEquals(expected, result);
	}

}
