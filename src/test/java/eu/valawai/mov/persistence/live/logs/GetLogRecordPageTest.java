/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.logs;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.api.v1.logs.LogRecordPage;
import eu.valawai.mov.api.v1.logs.LogRecordTest;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

/**
 * Test the operation to get some logs.
 *
 * @see GetLogRecordPage
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetLogRecordPageTest extends MasterOfValawaiTestCase {

	/**
	 * Create some logs that can be used.
	 */
	@BeforeAll
	public static void createlogs() {

		LogEntities.minLogs(100);
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecausenopOneMatchThePattern() {

		final var page = this.assertExecutionNotNull(
				GetLogRecordPage.fresh().withPattern("undefined Pattern that has not match any possible component"));
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.logs);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var offset = Integer.MAX_VALUE;
		final var total = this.assertItemNotNull(LogEntity.count());
		final var page = this.assertExecutionNotNull(GetLogRecordPage.fresh().withOffset(offset));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.logs);

	}

	/**
	 * Test get a log page.
	 */
	@Test
	public void shouldReturnPage() {

		final var expected = new LogRecordPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.empty();
		expected.total = LogEntities.nextLogsUntil(filter, max);

		final List<LogEntity> logs = this
				.assertItemNotNull(LogEntity.mongoCollection().find(filter, LogEntity.class).collect().asList());
		logs.sort((l1, l2) -> l1.id.compareTo(l2.id));
		expected.logs = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < logs.size(); i++) {

			final var log = logs.get(i);
			final var expectedLog = LogRecordTest.from(log);
			expected.logs.add(expectedLog);
		}

		final var page = this
				.assertExecutionNotNull(GetLogRecordPage.fresh().withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match some levels.
	 *
	 * @see LogLevel
	 */
	@Test
	public void shouldReturnPageWithLevel() {

		final var level1 = ValueGenerator.next(LogLevel.values());
		var level2 = ValueGenerator.next(LogLevel.values());
		while (level1 == level2) {

			level2 = ValueGenerator.next(LogLevel.values());
		}

		final var expected = new LogRecordPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.or(Filters.eq("level", level1), Filters.eq("level", level2));
		expected.total = LogEntities.nextLogsUntil(filter, max);

		final List<LogEntity> logs = this
				.assertItemNotNull(LogEntity.mongoCollection().find(filter, LogEntity.class).collect().asList());
		logs.sort((l1, l2) -> {

			var cmp = l2.level.name().compareTo(l1.level.name());
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.logs = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < logs.size(); i++) {

			final var log = logs.get(i);
			final var expectedLog = LogRecordTest.from(log);
			expected.logs.add(expectedLog);
		}

		final var page = this.assertExecutionNotNull(
				GetLogRecordPage.fresh().withLevel("/" + level1.name() + "|" + level2.name() + "/").withOrder("-level")
						.withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new LogRecordPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.regex("message", pattern);
		expected.total = LogEntities.nextLogsUntil(filter, max);

		final List<LogEntity> logs = this
				.assertItemNotNull(LogEntity.mongoCollection().find(filter, LogEntity.class).collect().asList());
		logs.sort((l1, l2) -> {

			var cmp = l2.message.compareTo(l1.message);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.logs = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < logs.size(); i++) {

			final var log = logs.get(i);
			final var expectedLog = LogRecordTest.from(log);
			expected.logs.add(expectedLog);
		}

		final var page = this.assertExecutionNotNull(GetLogRecordPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-message").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns¡ and a level.
	 */
	@Test
	public void shouldReturnPageWithPatternAndLevel() {

		final var pattern = ".*1.*";
		final var level = ValueGenerator.next(LogLevel.values());

		final var expected = new LogRecordPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(Filters.regex("message", pattern), Filters.eq("level", level));
		expected.total = LogEntities.nextLogsUntil(filter, max);

		final List<LogEntity> logs = this
				.assertItemNotNull(LogEntity.mongoCollection().find(filter, LogEntity.class).collect().asList());
		logs.sort((l1, l2) -> {

			var cmp = l2.message.compareTo(l1.message);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.logs = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < logs.size(); i++) {

			final var log = logs.get(i);
			final var expectedLog = LogRecordTest.from(log);
			expected.logs.add(expectedLog);
		}

		final var page = this.assertExecutionNotNull(GetLogRecordPage.fresh().withPattern("/" + pattern + "/")
				.withLevel(level.name()).withOrder("-message").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a component pattern.
	 */
	@Test
	public void shouldReturnPageWithComponentPattern() {

		final var pattern = ".*1.*";
		LogEntities.minLogs(100);
		final Uni<List<LogEntity>> find = LogEntity.findAll().list();
		final var logs = find.await().atMost(Duration.ofSeconds(30));
		final var expected = new LogRecordPage();
		expected.total = 0;
		expected.logs = new ArrayList<>();
		for (final var log : logs) {

			final var expectedLog = LogRecordTest.from(log);
			if (expectedLog.component != null
					&& (expectedLog.component.name != null && expectedLog.component.name.matches(pattern)
							|| expectedLog.component.description != null
									&& expectedLog.component.description.matches(pattern))) {
				expected.total++;
				expected.logs.add(expectedLog);

			}
		}

		expected.logs.sort((l1, l2) -> {

			var cmp = l1.component.type.name().compareTo(l2.component.type.name());
			if (cmp == 0) {

				cmp = l2.component.name.compareTo(l1.component.name);
				if (cmp == 0) {

					cmp = l1.component.description.compareTo(l2.component.description);
					if (cmp == 0) {

						cmp = Long.compare(l1.timestamp, l2.timestamp);
					}
				}
			}
			return cmp;
		});
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = Math.min(expected.offset + limit, expected.logs.size());

		expected.logs = expected.logs.subList(expected.offset, max);

		final var page = this.assertExecutionNotNull(GetLogRecordPage.fresh().withComponnetPattern("/" + pattern + "/")
				.withOrder("component.type,-component.name,component.description,timestamp").withOffset(expected.offset)
				.withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a component type.
	 */
	@Test
	public void shouldReturnPageWithComponentType() {

		final var type = "C1";
		LogEntities.minLogs(100);
		final Uni<List<LogEntity>> find = LogEntity.findAll().list();
		final var logs = find.await().atMost(Duration.ofSeconds(30));
		final var expected = new LogRecordPage();
		expected.total = 0;
		expected.logs = new ArrayList<>();
		for (final var log : logs) {

			final var expectedLog = LogRecordTest.from(log);
			if (expectedLog.component != null && expectedLog.component.type == ComponentType.C1) {
				expected.total++;
				expected.logs.add(expectedLog);
			}
		}

		expected.logs.sort((l1, l2) -> {

			var cmp = l2.component.name.compareTo(l1.component.name);
			if (cmp == 0) {

				cmp = l1.component.description.compareTo(l2.component.description);
				if (cmp == 0) {

					cmp = Long.compare(l1.timestamp, l2.timestamp);
				}
			}
			return cmp;
		});
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = Math.min(expected.offset + limit, expected.logs.size());

		expected.logs = expected.logs.subList(expected.offset, max);

		final var page = this.assertExecutionNotNull(GetLogRecordPage.fresh().withComponnetType(type)
				.withOrder("-component.name,component.description,timestamp").withOffset(expected.offset)
				.withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a component pattern.
	 */
	@Test
	public void shouldReturnPageWithComponentPatternAndType() {

		final var pattern = ".*1.*";
		final var type = "C1";
		LogEntities.minLogs(100);
		final Uni<List<LogEntity>> find = LogEntity.findAll().list();
		final var logs = find.await().atMost(Duration.ofSeconds(30));
		final var expected = new LogRecordPage();
		expected.total = 0;
		expected.logs = new ArrayList<>();
		for (final var log : logs) {

			final var expectedLog = LogRecordTest.from(log);
			if (expectedLog.component != null && expectedLog.component.type == ComponentType.C1
					&& (expectedLog.component.name != null && expectedLog.component.name.matches(pattern)
							|| expectedLog.component.description != null
									&& expectedLog.component.description.matches(pattern))) {
				expected.total++;
				expected.logs.add(expectedLog);

			}
		}

		expected.logs.sort((l1, l2) -> {

			var cmp = l1.component.type.name().compareTo(l2.component.type.name());
			if (cmp == 0) {

				cmp = l2.component.name.compareTo(l1.component.name);
				if (cmp == 0) {

					cmp = l1.component.description.compareTo(l2.component.description);
					if (cmp == 0) {

						cmp = Long.compare(l1.timestamp, l2.timestamp);
					}
				}
			}
			return cmp;
		});
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = Math.min(expected.offset + limit, expected.logs.size());

		expected.logs = expected.logs.subList(expected.offset, max);

		final var page = this.assertExecutionNotNull(
				GetLogRecordPage.fresh().withComponnetType(type).withComponnetPattern("/" + pattern + "/")
						.withOrder("component.type,-component.name,component.description,timestamp")
						.withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns¡ and a level.
	 */
	@Test
	public void shouldReturnPageThatMatchesMessageComponentLevelAndType() {

		final var msgPattern = ".*" + rnd().nextInt(0, 10) + ".*";
		final var componentPattern = ".*" + rnd().nextInt(0, 10) + ".*";
		final var level = next(LogLevel.values());
		final var type = next(ComponentType.values());
		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var filter = Filters.and(Filters.regex("name", componentPattern), Filters.eq("type", type));
		final var count = 10 + offset + limit;
		ComponentEntities.nextComponentsUntil(filter, count);
		final var components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		LogEntities.nextLogs(count + 10, components);

		final var page = this.assertExecutionNotNull(
				GetLogRecordPage.fresh().withPattern("/" + msgPattern + "/").withLevel(level.name())
						.withComponnetType(type.name()).withComponnetPattern("/" + componentPattern + "/")
						.withOrder("-message,level,compoennt.name,-timestamp").withOffset(offset).withLimit(limit));
		assertNotNull(page);
		assertNotNull(page.total);
		assertNotNull(page.logs);
		assertEquals(offset, page.offset);
		assertTrue(page.total >= page.logs.size());

		for (final var log : page.logs) {

			assertTrue(log.message.matches(msgPattern));
			assertEquals(log.level, level);
			assertNotNull(log.component);
			assertEquals(log.component.type, type);
			assertTrue(log.component.name.matches(componentPattern));
		}

	}

}
