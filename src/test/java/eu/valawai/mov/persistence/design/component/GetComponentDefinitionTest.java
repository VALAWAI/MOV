/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v2.design.components.ComponentDefinitionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetComponentDefinition}.
 *
 * @see GetComponentDefinition
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetComponentDefinitionTest extends MovPersistenceTestCase {

	/**
	 * Should not get from undefined topology.
	 */
	@Test
	public void shouldNotGetUndefinedTopology() {

		final var undefined = ComponentDefinitionEntities.undefined();
		this.assertItemIsNull(GetComponentDefinition.fresh().withId(undefined).execute());

	}

	/**
	 * Should get topology.
	 */
	@Test
	public void shouldGetComponentDefinition() {

		final var entity = ComponentDefinitionEntities.minComponents(1).get(0);
		final var found = this.assertItemNotNull(GetComponentDefinition.fresh().withId(entity.id).execute());
		final var expected = ComponentDefinitionTest.from(entity);
		assertEquals(found, expected);
	}

}
