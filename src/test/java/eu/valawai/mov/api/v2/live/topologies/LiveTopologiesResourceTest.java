/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link LiveTopologiesResource}.
 *
 * @see LiveTopologiesResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class LiveTopologiesResourceTest extends APITestCase {

	/**
	 * Populate with some data.
	 */
	@BeforeAll
	public static void populate() {

		TopologyConnectionEntities.minTopologyConnections(20);
	}

	/**
	 * Should not get the live topology with a bad offset.
	 *
	 * @see LiveTopologiesResource#getLiveTopology(int, int)
	 */
	@Test
	public void shouldNotGetLiveTopologyWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v2/live/topologies").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get the live topology with a bad limit.
	 *
	 * @see LiveTopologiesResource#getLiveTopology(int, int)
	 */
	@Test
	public void shouldNotGetLiveTopologyWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v2/live/topologies").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get the live topology with the default page.
	 *
	 * @see LiveTopologiesResource#getLiveTopology(int, int)
	 */
	@Test
	public void shouldGetLiveTopologyWithDefaultPage() {

		final var found = given().when().get("/v2/live/topologies").then().statusCode(Status.OK.getStatusCode())
				.extract().as(LiveTopology.class);
		final var expected = LiveTopologyTest.current(0, 100);
		assertEquals(expected, found);

	}

	/**
	 * Should get the live topology with a specific page.
	 *
	 * @see LiveTopologiesResource#getLiveTopology(int, int)
	 */
	@Test
	public void shouldGetLiveTopologyWithSomeLimitAndOffset() {

		final var found = given().when().queryParam("offset", "1").queryParam("limit", "5").get("/v2/live/topologies")
				.then().statusCode(Status.OK.getStatusCode()).extract().as(LiveTopology.class);
		final var expected = LiveTopologyTest.current(1, 5);
		assertEquals(expected, found);

	}

}
