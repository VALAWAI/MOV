/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.logs.LogRecordTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link LogRecordRepository}.
 *
 * @see LogRecordRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class LogRecordRepositoryTest extends MovPersistenceTestCase {

	/**
	 * The rpository to test.
	 */
	@Inject
	LogRecordRepository repository;

	/**
	 * Should not add bad log.
	 */
	@Test
	public void shouldNotAddBadLog() {

		assertFalse(this.repository.add(null));
	}

	/**
	 * Should add log.
	 */
	@Test
	public void shouldAddLog() {

		final var expected = new LogRecordTest().nextModel();
		assertTrue(this.repository.add(expected));
		assertEquals(expected, this.repository.last());

	}

}
