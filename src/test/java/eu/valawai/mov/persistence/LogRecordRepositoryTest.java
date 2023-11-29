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

import java.util.ArrayList;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.api.v1.logs.LogRecord;
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
	 * The repository to test.
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
	 * Should add a log.
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
	public void shouldAddLogAndDiscard() {

		var first = this.repository.first();
		final var builder = new LogRecordTest();
		while (this.repository.count() < this.maxLogs) {

			final var expected = builder.nextModel();
			final var now = TimeManager.now();
			assertTrue(this.repository.add(expected));
			final var last = this.repository.last();
			assertTrue(last.timestamp >= now);
			expected.timestamp = last.timestamp;
			assertEquals(expected, last);

		}

		for (var i = 0; i < 10; i++) {

			final var expected = builder.nextModel();
			assertTrue(this.repository.add(expected));
			final var now = TimeManager.now();
			assertTrue(this.repository.add(expected));
			assertEquals(this.maxLogs, this.repository.count());
			final var last = this.repository.last();
			assertTrue(last.timestamp >= now);
			expected.timestamp = last.timestamp;
			assertEquals(expected, last);
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
		final var emptyPage = new LogRecordPage();
		assertEquals(emptyPage, this.repository.getLogRecordPage(null, null, null, 0, 20));
		emptyPage.offset = 3;
		assertEquals(emptyPage, this.repository.getLogRecordPage(null, null, ",", emptyPage.offset, 20));
	}

	/**
	 * Should get a page.
	 */
	@Test
	public void shouldGetAPage() {

		final var expected = new LogRecordPage();
		final var all = new ArrayList<LogRecord>();
		expected.logs = all;
		expected.total = 20;
		this.repository.clear();
		final var builder = new LogRecordTest();
		for (var i = 0; i < expected.total; i++) {

			assertTrue(this.repository.add(builder.nextModel()));
			final var last = this.repository.last();
			expected.logs.add(last);

		}

		assertEquals(expected, this.repository.getLogRecordPage(null, null, null, 0, all.size()));

		expected.logs = null;
		expected.offset = all.size();
		assertEquals(expected, this.repository.getLogRecordPage(null, null, null, expected.offset, all.size()));

		expected.logs = all.subList(1, 4);
		expected.offset = 1;
		assertEquals(expected, this.repository.getLogRecordPage(null, null, null, expected.offset, 3));

		expected.logs = all;
		expected.offset = 0;
		expected.logs.sort((l1, l2) -> {

			var cmp = Long.compare(l1.timestamp, l2.timestamp);
			if (cmp == 0) {

				cmp = l1.level.compareTo(l2.level);
				if (cmp == 0) {

					cmp = l1.message.compareTo(l2.message);
				}
			}
			return cmp;
		});
		assertEquals(expected, this.repository.getLogRecordPage(null, null, "timestamp,level,message", 0, all.size()));

		expected.logs.sort((l1, l2) -> {

			var cmp = Long.compare(l2.timestamp, l1.timestamp);
			if (cmp == 0) {

				cmp = l2.level.compareTo(l1.level);
				if (cmp == 0) {

					cmp = l2.message.compareTo(l1.message);
				}
			}
			return cmp;
		});
		assertEquals(expected, this.repository.getLogRecordPage(null, null, "-timestamp,-level,-message,payload", 0,
				expected.logs.size()));

		expected.logs = all.stream().filter(l -> l.level == LogLevel.ERROR).toList();
		expected.total = expected.logs.size();
		assertEquals(expected,
				this.repository.getLogRecordPage(null, "error", "-timestamp,-level,-message", 0, all.size()));

		expected.offset = 1;
		if (expected.total > 5) {

			expected.logs = expected.logs.subList(1, 5);
			assertEquals(expected,
					this.repository.getLogRecordPage(null, "error", "-timestamp,-level,-message", expected.offset, 4));
		}

		expected.offset = 1;
		expected.logs = all.subList(expected.offset, 5);
		expected.total = all.size();
		assertEquals(expected,
				this.repository.getLogRecordPage(null, "/.*o.*/", "-timestamp,-level,-message", expected.offset, 4));

		expected.logs = all.stream().filter(l -> l.message.matches(".*1.*")).toList();
		expected.total = expected.logs.size();
		expected.offset = 0;
		assertEquals(expected,
				this.repository.getLogRecordPage("/.*1.*/", null, "-timestamp,-level,-message", 0, all.size()));

	}

}
