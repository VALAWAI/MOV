/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.api.EnvironmentResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link EnvironmentResource}.
 *
 * @see EnvironmentResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class EnvironmentResourceTest extends APITestCase {

	/**
	 * The URL of the application.
	 */
	@ConfigProperty(name = "mov.url", defaultValue = "http://${quarkus.http.host}:${quarkus.http.port}")
	String movUrl;

	/**
	 * Should get the environment file.
	 */
	@Test
	public void shouldGetEnvironemnt() {

		final var received = given().accept("text/javascript").when().get("/env.js").then()
				.statusCode(Status.OK.getStatusCode()).extract().asString();
		assertNotNull(received);
		assertTrue(received.contains(this.movUrl));

	}

	/**
	 * Should get the environment file.
	 */
	@Test
	public void shouldNotGetEnvironemnt() {

		given().accept("application/json").when().get("/env.js").then()
				.statusCode(Status.NOT_ACCEPTABLE.getStatusCode());

	}

}
