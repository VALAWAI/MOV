/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetComponentsLibraryStatus}.
 *
 * @see GetComponentsLibraryStatus
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetComponentsLibraryStatusTest extends MovPersistenceTestCase {

	/**
	 * Should get the status.
	 */
	@Test
	public void shouldGetStatus() {

		final var total = ComponentDefinitionEntities.count();
		final var oldestComponentTimestamp = ComponentDefinitionEntities.oldestComponentTimestamp();
		final var newestComponentTimestamp = ComponentDefinitionEntities.newestComponentTimestamp();
		final var status = this.assertItemNotNull(GetComponentsLibraryStatus.fresh().execute());

		assertTrue(total >= status.componentCount);
		assertTrue(status.oldestComponentTimestamp <= status.newestComponentTimestamp);
		assertTrue(oldestComponentTimestamp <= status.oldestComponentTimestamp);
		assertTrue(newestComponentTimestamp >= status.newestComponentTimestamp);

	}

}
