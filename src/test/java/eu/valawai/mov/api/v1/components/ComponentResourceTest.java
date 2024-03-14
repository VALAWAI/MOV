/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.rnd;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
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
	@BeforeAll
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
	 * Should get page with pattern and type.
	 */
	@Test
	public void shouldGetPageWithPatternAndType() {

		final var pattern = ".*1.*";
		final var type = next(ComponentType.values());

		final var expected = new MinComponentPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)),
				Filters.eq("type", type));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l1.description.compareTo(l2.description);
			if (cmp == 0) {

				cmp = l2.name.compareTo(l1.name);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = given().when().queryParam("pattern", "/" + pattern + "/").queryParam("type", type.name())
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "description,-name").get("/v1/components").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(MinComponentPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var expected = new MinComponentPage();
		expected.offset = 0;

		final var limit = 20;
		final var max = expected.offset + limit + 10;
		final var filter = Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = Long.compare(l1.since, l2.since);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = given().when().get("/v1/components").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinComponentPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should not get a n undefined component.
	 */
	@Test
	public void shouldNotFoundUndefinedComponent() {

		final var id = nextObjectId().toHexString();
		given().when().get("/v1/components/" + id).then().statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Should get a component.
	 */
	@Test
	public void shouldGetComponent() {

		final var component = ComponentEntities.nextComponent();
		final var expected = ComponentTest.from(component);
		final var id = component.id.toHexString();
		final var result = given().when().get("/v1/components/" + id).then().statusCode(Status.OK.getStatusCode())
				.extract().as(Component.class);
		assertEquals(expected, result);

	}

	/**
	 * Should unregister a component.
	 */
	@Test
	public void shouldUnregisterAComponent() {

		final var component = ComponentEntities.nextComponent();
		final var id = component.id.toHexString();
		given().when().delete("/v1/components/" + id).then().statusCode(Status.NO_CONTENT.getStatusCode());

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> {
			// Nothing to do
		});
		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and timestamp >= ?2", LogLevel.INFO, now)));

		final ComponentEntity updated = this.assertItemNotNull(ComponentEntity.findById(component.id));
		assertNotNull(updated.finishedTime);
		assertTrue(now <= updated.finishedTime);

	}

}
