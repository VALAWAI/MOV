/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.logs;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.persistence.LogRecordRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link LogResource}.
 *
 * @see LogResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class LogResourceTest {

	/**
	 * The repository with the logs.
	 */
	@Inject
	LogRecordRepository repository;

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
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var currentPage = given().when().get("/v1/logs").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LogRecordPage.class);
		assertNotNull(currentPage);

		final var offset = 3;
		final var limit = 7;
		final var pattern = ".*1.*";
		final var expected = new LogRecordPage();
		expected.offset = offset;
		if (currentPage.logs != null) {

			expected.logs = currentPage.logs.stream()
					.filter(log -> log.level == LogLevel.INFO && log.message.matches(pattern)).toList();
		} else {

			expected.logs = new ArrayList<>();

		}
		final var max = offset + limit + 3;
		final var builder = new LogRecordTest();
		while (expected.logs.size() < max) {

			this.repository.add(builder.nextModel());
			final var log = this.repository.last();
			if (log.level == LogLevel.INFO && log.message.matches(pattern)) {

				expected.logs.add(log);
			}
		}

		expected.total = expected.logs.size();
		expected.logs.sort((l1, l2) -> {

			var cmp = Long.compare(l2.timestamp, l1.timestamp);
			if (cmp == 0) {

				cmp = l1.message.compareTo(l2.message);
			}

			return cmp;

		});
		expected.logs = expected.logs.subList(offset, offset + limit);

		final var page = given().when().queryParam("pattern", "/" + pattern + "/").queryParam("level", "info")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-timestamp,message").get("/v1/logs").then().statusCode(Status.OK.getStatusCode())
				.extract().as(LogRecordPage.class);
		assertEquals(expected, page);
	}

}
