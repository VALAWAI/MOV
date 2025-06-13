/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link FinishAllComponents}.
 *
 * @see FinishAllComponents
 *
 * @author VALAWAI
 */
@QuarkusTest
public class FinishAllComponentsTest extends MovPersistenceTestCase {

	/**
	 * Check that enable component.
	 */
	@Test
	public void shouldFinishAllComponents() {

		final var components = ComponentEntities.nextComponents(10);
		final var now = TimeManager.now();
		this.assertItemIsNull(FinishAllComponents.fresh().execute());

		for (final var component : components) {

			final ComponentEntity updated = (ComponentEntity) ComponentEntity.findById(component.id).await()
					.atMost(Duration.ofSeconds(30));
			assertNotNull(updated);
			assertNotNull(updated.finishedTime);
			assertTrue(now <= updated.finishedTime);
		}

	}

}
