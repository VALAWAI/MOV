/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntities;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link TopologiesResource}.
 *
 * @see TopologiesResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class TopologiesResourceTest extends APITestCase {

	/**
	 * Create some topologies that can be used.
	 */
	@BeforeAll
	public static void createTopologies() {

		TopologyGraphEntities.minTopologies(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v2/design/topologies").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v2/design/topologies").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v2/design/topologies").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v2/design/topologies").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinTopologyPage.class);
		final var expected = new MinTopologyPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page.
	 */
	@Test
	public void shouldGetPage() {

		final var expected = new MinTopologyPage();
		expected.offset = 0;

		final var limit = 20;
		final var max = expected.offset + limit + 10;
		final var pattern = ".*1.*";
		final var filter = Filters.regex("name", pattern);
		expected.total = TopologyGraphEntities.nextTopologyGraphsUntil(filter, max);

		final List<TopologyGraphEntity> topologies = this.assertItemNotNull(
				TopologyGraphEntity.mongoCollection().find(filter, TopologyGraphEntity.class).collect().asList());
		topologies.sort((l1, l2) -> {

			var cmp = 0;
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.topologies = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < topologies.size(); i++) {

			final var topology = topologies.get(i);
			final var expectedTopology = MinTopologyTest.from(topology);
			expected.topologies.add(expectedTopology);
		}

		final var page = given().when().queryParam("pattern", "/" + pattern + "/").queryParam("limit", limit)
				.get("/v2/design/topologies").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinTopologyPage.class);
		assertEquals(expected, page);

	}

}
