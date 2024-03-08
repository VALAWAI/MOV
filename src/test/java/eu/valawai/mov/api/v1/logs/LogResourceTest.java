/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.logs.LogEntities;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link LogResource}.
 *
 * @see LogResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class LogResourceTest extends APITestCase {

	/**
	 * Create some logs that can be used.
	 */
	@BeforeAll
	public static void createLogs() {

		LogEntities.minLogs(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v1/logs").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v1/logs").then().statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v1/logs").then().statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v1/logs").then().statusCode(Status.OK.getStatusCode()).extract().as(LogRecordPage.class);
		final var expected = new LogRecordPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page with some message pattern.
	 */
	@Test
	public void shouldGetPageWithPattern() {

		final var pattern = ".*" + ValueGenerator.rnd().nextInt(0, 10) + ".*";
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

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-message").get("/v1/logs").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LogRecordPage.class);
		assertEquals(expected, page);
	}

	/**
	 * Should get page with some specific log level.
	 */
	@Test
	public void shouldGetPageWithLevel() {

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

		final var page = given().when().queryParam("level", "/" + level1.name() + "|" + level2.name() + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-level").get("/v1/logs").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LogRecordPage.class);
		assertEquals(expected, page);
	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var pattern = ".*" + ValueGenerator.rnd().nextInt(0, 10) + ".*";
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

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("level", "/" + level + "/").queryParam("limit", String.valueOf(limit))
				.queryParam("offset", String.valueOf(expected.offset)).queryParam("order", "-message").get("/v1/logs")
				.then().statusCode(Status.OK.getStatusCode()).extract().as(LogRecordPage.class);
		assertEquals(expected, page);
	}

}
