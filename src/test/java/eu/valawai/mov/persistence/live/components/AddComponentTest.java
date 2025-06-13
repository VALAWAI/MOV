/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ComponentTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.live.components.AddComponent;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

/**
 * Test the operation to add a component.
 *
 * @see AddComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class AddComponentTest extends MovPersistenceTestCase {

	/**
	 * Check that add a component.
	 */
	@Test
	public void shouldAddComponent() {

		final var component = new ComponentTest().nextModel();
		final var now = TimeManager.now();
		final var added = AddComponent.fresh().withComponent(component).execute().await()
				.atMost(Duration.ofSeconds(30));
		assertNotNull(added);
		assertNotNull(added.id);

		final Uni<ComponentEntity> find = ComponentEntity.findById(added.id);
		final var entity = find.await().atMost(Duration.ofSeconds(30));
		assertNotNull(entity);
		assertTrue(now <= entity.since);
		final var result = ComponentTest.from(entity);
		component.id = entity.id;
		component.since = entity.since;
		assertEquals(component, result);

	}
}
