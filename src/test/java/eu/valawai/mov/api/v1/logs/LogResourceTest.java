/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.logs;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.logs.LogEntities;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link LogResource}.
 *
 * @see LogResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class LogResourceTest extends APITestCase {

	/**
	 * Create some logs that can be used.
	 */
	@BeforeClass
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

		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var pattern = ".*" + ValueGenerator.rnd().nextInt(0, 10) + ".*";
		final var expected = new LogRecordPage();
		expected.offset = offset;

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-message").get("/v1/logs").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LogRecordPage.class);
		final var total = this.assertItemNotNull(LogEntity.count("message like ?1", pattern));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var logs = this.assertItemNotNull(LogEntity
				.find("message like ?1", Sort.descending("message").and("_id", Sort.Direction.Ascending), pattern)
				.page(Page.of(offset, limit)).list());
		if (!logs.isEmpty()) {

			assertEquals(logs, page.logs);

		} else {

			assertNull(page.logs);

		}
	}

	/**
	 * Should get page with some specific log level.
	 */
	@Test
	public void shouldGetPageWithLevel() {

		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var level = ValueGenerator.next(LogLevel.values()).name();
		final var expected = new LogRecordPage();
		expected.offset = offset;

		final var page = given().when().queryParam("level", level).queryParam("limit", String.valueOf(limit))
				.queryParam("offset", String.valueOf(expected.offset)).queryParam("order", "-message").get("/v1/logs")
				.then().statusCode(Status.OK.getStatusCode()).extract().as(LogRecordPage.class);
		final var total = this.assertItemNotNull(LogEntity.count("level = ?1", level));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var logs = this.assertItemNotNull(
				LogEntity.find("level = ?1", Sort.descending("message").and("_id", Sort.Direction.Ascending), level)
						.page(Page.of(offset, limit)).list());
		if (!logs.isEmpty()) {

			assertEquals(logs, page.logs);

		} else {

			assertNull(page.logs);

		}
	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var pattern = ".*" + ValueGenerator.rnd().nextInt(0, 10) + ".*";
		final var level = ".*" + ValueGenerator.next(LogLevel.values()).name().substring(1, 3) + ".*";
		final var expected = new LogRecordPage();
		expected.offset = offset;

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("level", "/" + level + "/").queryParam("limit", String.valueOf(limit))
				.queryParam("offset", String.valueOf(expected.offset)).queryParam("order", "-message").get("/v1/logs")
				.then().statusCode(Status.OK.getStatusCode()).extract().as(LogRecordPage.class);
		final var total = this.assertItemNotNull(LogEntity.count("message like ?1 and level like ?2", pattern, level));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var logs = this.assertItemNotNull(LogEntity
				.find("message like ?1 and level like ?2",
						Sort.descending("message").and("_id", Sort.Direction.Ascending), pattern, level)
				.page(Page.of(offset, limit)).list());
		if (!logs.isEmpty()) {

			assertEquals(logs, page.logs);

		} else {

			assertNull(page.logs);

		}
	}

}
