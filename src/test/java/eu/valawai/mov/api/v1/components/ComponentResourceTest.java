/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.persistence.ComponentRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link ComponentResource}.
 *
 * @see ComponentResource
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class ComponentResourceTest {

	/**
	 * The repository with the components.
	 */
	@Inject
	ComponentRepository repository;

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
				.get("/v1/components").then().statusCode(Status.OK.getStatusCode()).extract().as(ComponentPage.class);
		final var expected = new ComponentPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var currentPage = given().when().get("/v1/components").then().statusCode(Status.OK.getStatusCode())
				.extract().as(ComponentPage.class);
		assertNotNull(currentPage);

		final var offset = 3;
		final var limit = 7;
		final var pattern = ".*1.*";
		final var expected = new ComponentPage();
		expected.offset = offset;
		if (currentPage.components != null) {

			expected.components = currentPage.components.stream().filter(component -> component.name.matches(pattern))
					.toList();
		} else {

			expected.components = new ArrayList<>();

		}
		final var max = offset + limit + 3;
		final var builder = new ComponentTest();
		while (expected.components.size() < max) {

			this.repository.add(builder.nextModel());
			final var component = this.repository.last();
			if (component.name.matches(pattern)) {

				expected.components.add(component);
			}
		}

		expected.total = expected.components.size();
		expected.components.sort((c1, c2) -> {

			var cmp = Long.compare(c2.since, c1.since);
			if (cmp == 0) {

				cmp = c1.name.compareTo(c2.name);
			}

			return cmp;

		});
		expected.components = expected.components.subList(offset, offset + limit);

		final var page = given().when().queryParam("pattern", "/" + pattern + "/").queryParam("level", "info")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "-since,name").get("/v1/components").then().statusCode(Status.OK.getStatusCode())
				.extract().as(ComponentPage.class);
		assertEquals(expected, page);
	}

}
