/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.components.ComponentEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link ComponentResource}.
 *
 * @see ComponentResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ComponentResourceTest extends APITestCase {

	/**
	 * Create some components that can be used.
	 */
	@BeforeClass
	public static void createComponents() {

		ComponentEntities.minComponents(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v1/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v1/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v1/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v1/components").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinComponentPage.class);
		final var expected = new MinComponentPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var pattern = ".*" + ValueGenerator.rnd().nextInt(0, 10) + ".*";
		final var expected = new MinComponentPage();
		expected.offset = offset;

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-name").get("/v1/components").then().statusCode(Status.OK.getStatusCode())
				.extract().as(MinComponentPage.class);
		final var total = this.assertItemNotNull(ComponentEntity.count("name like ?1 or description like ?1", pattern));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var components = this.assertItemNotNull(ComponentEntity
				.find("name like ?1 or description like ?1",
						Sort.descending("name").and("_id", Sort.Direction.Ascending), pattern)
				.page(Page.of(offset, limit)).list());
		if (!components.isEmpty()) {

			assertEquals(components, page.components);

		} else {

			assertNull(page.components);

		}

	}

}
