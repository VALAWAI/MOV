/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link LiveConnectionsResource}.
 *
 * @see LiveConnectionsResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class LiveConnectionsResourceTest extends APITestCase {

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v2/live/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v2/live/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v2/live/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get an undefined connection.
	 */
	@Test
	public void shouldNotGetUndefinedConnection() {

		final var undefined = TopologyConnectionEntities.undefined();
		given().when().get("/v2/live/connections/" + undefined.toHexString()).then()
				.statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Should get a connection.
	 */
	@Test
	public void shouldGetConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var found = given().when().get("/v2/live/connections/" + connection.id.toHexString()).then()
				.statusCode(Status.OK.getStatusCode()).extract().as(LiveConnection.class);
		assertThat(found, is(not(nullValue())));
		final var expected = LiveConnectionTest.from(connection);
		assertThat(found, is(expected));
	}

}
