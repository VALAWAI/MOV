/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.APITestCase;
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
	 * Should get the live topology.
	 *
	 * @see LiveTopologiesResource#getLiveTopology()
	 */
	@Test
	public void shouldGetLiveTopology() {

		final var found = given().when().get("/v2/live/topologies").then().statusCode(Status.OK.getStatusCode())
				.extract().as(LiveTopology.class);
		final var expected = LiveTopologyTest.current();
		assertEquals(expected, found);

	}

}
