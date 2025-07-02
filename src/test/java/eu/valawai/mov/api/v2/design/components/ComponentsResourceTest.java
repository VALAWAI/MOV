/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import static eu.valawai.mov.ValueGenerator.next;
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
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntities;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link ComponentsResource}.
 *
 * @see ComponentsResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ComponentsResourceTest extends APITestCase {

	/**
	 * Create some components that can be used.
	 */
	@BeforeAll
	public static void createComponents() {

		ComponentDefinitionEntities.minComponents(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v2/design/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v2/design/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v2/design/components").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v2/design/components").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(ComponentDefinitionPage.class);
		final var expected = new ComponentDefinitionPage();
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

		final var expected = new ComponentDefinitionPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)),
				Filters.eq("type", type));
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> components = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
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
			final var expectedComponent = ComponentDefinitionTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = given().when().queryParam("pattern", "/" + pattern + "/").queryParam("type", type.name())
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "description,-name").get("/v2/design/components").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(ComponentDefinitionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var expected = new ComponentDefinitionPage();
		expected.offset = 0;

		final var limit = 20;
		final var max = expected.offset + limit + 10;
		final var type = ValueGenerator.next(ComponentType.values());
		final var filter = Filters.eq("type", type);
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> components = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = 0;
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = ComponentDefinitionTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = given().when().queryParam("type", type.name()).get("/v2/design/components").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(ComponentDefinitionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should get current components library status.
	 */
	@Test
	public void shouldGetCurrentComponentsLibraryStatus() {

		final var total = ComponentDefinitionEntities.count();
		final var oldestComponentTimestamp = ComponentDefinitionEntities.oldestComponentTimestamp();
		final var newestComponentTimestamp = ComponentDefinitionEntities.newestComponentTimestamp();

		final var status = given().when().get("/v2/design/components/library").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(ComponentsLibraryStatus.class);
		assertNotNull(status);

		assertTrue(total >= status.componentCount);
		assertTrue(status.oldestComponentTimestamp <= status.newestComponentTimestamp);
		assertTrue(oldestComponentTimestamp <= status.oldestComponentTimestamp);
		assertTrue(newestComponentTimestamp >= status.newestComponentTimestamp);

	}

	/**
	 * Should update components library status.
	 *
	 * @see ComponentsResource#refreshComponentsLibrary
	 */
	@Test
	public void shouldRefreshComponentsLibrary() {

		final var now = TimeManager.now();
		given().when().delete("/v2/design/components/library").then().statusCode(Status.NO_CONTENT.getStatusCode());

		this.waitUntil(() -> ComponentDefinitionEntities.newestComponentTimestamp(), time -> time >= now);

	}

}
