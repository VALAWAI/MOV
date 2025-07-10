/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v2.design.components.ComponentDefinitionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link UpdateComponentDefinition}.
 *
 * @see UpdateComponentDefinition
 *
 * @author VALAWAI
 */
@QuarkusTest
public class UpdateComponentDefinitionTest extends MovPersistenceTestCase {

	/**
	 * Should not get from undefined component.
	 */
	@Test
	public void shouldNotUpdateUndefinedComponentDefinition() {

		final var undefined = ComponentDefinitionEntities.undefined();
		final var component = ComponentDefinitionTest.from(ComponentDefinitionEntities.minComponents(1).get(0));

		final var updated = this.assertItemNotNull(
				UpdateComponentDefinition.fresh().withId(undefined).withComponentDefinition(component).execute());
		assertFalse(updated, "Updated an undefined component");

	}

	/**
	 * Should update component.
	 */
	@Test
	public void shouldUpdateComponentDefinition() {

		final var entities = ComponentDefinitionEntities.minComponents(2);
		final var targetId = entities.get(1).id;
		final var component = ComponentDefinitionTest.from(entities.get(0));
		final var updated = this.assertItemNotNull(
				UpdateComponentDefinition.fresh().withId(targetId).withComponentDefinition(component).execute());
		assertTrue(updated, "Not updated a component");

		final ComponentDefinitionEntity unmodified = this
				.assertItemNotNull(ComponentDefinitionEntity.findById(component.id));
		final var unmodifiedComponentDefinition = ComponentDefinitionTest.from(unmodified);
		component.updatedAt = unmodified.updatedAt;
		assertEquals(component, unmodifiedComponentDefinition);

		final ComponentDefinitionEntity current = this.assertItemNotNull(ComponentDefinitionEntity.findById(targetId));
		final var currentComponentDefinition = ComponentDefinitionTest.from(current);
		component.id = current.id;
		component.updatedAt = current.updatedAt;
		assertEquals(component, currentComponentDefinition);

	}

}
