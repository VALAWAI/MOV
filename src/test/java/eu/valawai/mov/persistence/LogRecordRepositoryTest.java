/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogRecordPage;
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
	 * The number maximum of logs to store.
	 */
	@ConfigProperty(name = "valawai.logs.max", defaultValue = "10000")
	int maxLogs;

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

		final var expectedCount = this.repository.count() + 1;
		final var expected = new LogRecordTest().nextModel();
		final var now = TimeManager.now();
		assertTrue(this.repository.add(expected));
		final var last = this.repository.last();
		assertTrue(last.timestamp >= now);
		expected.timestamp = last.timestamp;
		assertEquals(expected, last);
		assertEquals(expectedCount, this.repository.count());

	}

	/**
	 * Should add logs and discard if reached the maximum.
	 */
	@Test
	public void shouldAddLogAndDiscsard() {

		var first = this.repository.first();
		for (var i = this.repository.count(); i < this.maxLogs; i++) {

			final var expected = new LogRecordTest().nextModel();
			final var now = TimeManager.now();
			assertTrue(this.repository.add(expected));
			final var last = this.repository.last();
			assertTrue(last.timestamp >= now);
			expected.timestamp = last.timestamp;
			assertEquals(expected, last);
			assertEquals(i + 1, this.repository.count());

		}

		for (var i = 0; i < 10; i++) {

			final var expected = new LogRecordTest().nextModel();
			assertTrue(this.repository.add(expected));
			final var now = TimeManager.now();
			assertTrue(this.repository.add(expected));
			final var last = this.repository.last();
			assertTrue(last.timestamp >= now);
			expected.timestamp = last.timestamp;
			assertEquals(expected, last);
			assertEquals(this.maxLogs, this.repository.count());
			final var newFirst = this.repository.first();
			assertNotEquals(first, newFirst);
			first = newFirst;

		}

	}

	/**
	 * Should clear logs.
	 */
	@Test
	public void shouldClearLogs() {

		this.repository.clear();
		assertEquals(0, this.repository.count());
		assertNull(this.repository.last());
		assertNull(this.repository.first());
		assertEquals(new LogRecordPage(), this.repository.getLogRecordPage(null, null, null, 0, 20));
	}

}
