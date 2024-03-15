/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.rnd;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link TopologyResource}.
 *
 * @see TopologyResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class TopologyResourceTest extends APITestCase {

	/**
	 * Create some connections that can be used.
	 */
	@BeforeAll
	public static void createConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v1/topology/connections").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinConnectionPage.class);
		final var expected = new MinConnectionPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page with pattern.
	 */
	@Test
	public void shouldGetPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new MinConnectionPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.regex("source.channelName", pattern), Filters.regex("target.channelName", pattern)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l1.target.channelName.compareTo(l2.target.channelName);
			if (cmp == 0) {

				cmp = l2.source.channelName.compareTo(l1.source.channelName);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "target,-source").get("/v1/topology/connections").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(MinConnectionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should get page for a component.
	 */
	@Test
	public void shouldGetPageWithComponentId() {

		final var expected = new MinConnectionPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;

		final var component = ComponentEntities.nextComponent();
		for (var i = 0; i < max; i++) {

			final var connection = TopologyConnectionEntities.nextTopologyConnection();
			if (i % 2 == 0) {

				connection.source.componentId = component.id;

			} else {

				connection.target.componentId = component.id;
			}
			this.assertItemNotNull(connection.update());
			if (connection.deletedTimestamp != null) {

				i--;
			}
		}

		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.eq("source.componentId", component.id),
						Filters.eq("target.componentId", component.id)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l1.target.channelName.compareTo(l2.target.channelName);
			if (cmp == 0) {

				cmp = l2.source.channelName.compareTo(l1.source.channelName);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = given().when().queryParam("component", component.id.toHexString())
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "target,-source").get("/v1/topology/connections").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(MinConnectionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should not get an undefined connection.
	 */
	@Test
	public void shouldNotFoundUndefinedConnection() {

		final var id = nextObjectId().toHexString();
		given().when().get("/v1/topology/connections/" + id).then().statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Should get a connection.
	 */
	@Test
	public void shouldGetConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var expected = TopologyConnectionTest.from(connection);
		final var id = connection.id.toHexString();
		final var result = given().when().get("/v1/topology/connections/" + id).then()
				.statusCode(Status.OK.getStatusCode()).extract().as(TopologyConnection.class);
		assertEquals(expected, result);

	}

}
