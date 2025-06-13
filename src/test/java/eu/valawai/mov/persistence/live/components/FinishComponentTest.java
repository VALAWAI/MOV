/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.components.FinishComponent;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link FinishComponent}.
 *
 * @see FinishComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class FinishComponentTest extends MovPersistenceTestCase {

	/**
	 * Check that cannot enable with undefined component.
	 */
	@Test
	public void shouldNotEnableUndefinedComponent() {

		final var componentId = ValueGenerator.nextObjectId();
		final var result = this.assertItemNotNull(FinishComponent.fresh().withComponent(componentId).execute());
		assertFalse(result);
	}

	/**
	 * Check that enable component.
	 */
	@Test
	public void shouldFinishComponent() {

		final var component = ComponentEntities.nextComponent();
		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(FinishComponent.fresh().withComponent(component.id).execute());
		assertTrue(result);

		final ComponentEntity updated = (ComponentEntity) ComponentEntity.findById(component.id).await()
				.atMost(Duration.ofSeconds(30));
		assertNotNull(updated);
		assertNotNull(updated.finishedTime);
		assertTrue(now <= updated.finishedTime);

	}

}
