/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkiverse.quinoa.testing.QuinoaTestProfiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link OnStart}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@TestProfile(QuinoaTestProfiles.Enable.class)
public class OnStartTest {

	/**
	 * Test not get undefined index.
	 */
	@Test
	public void shouldNotGetUndefinedIndex() {

		given().when().get("/nz/index.html").then().statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Test not delete an undefined treatment.
	 */
	@Test
	public void shouldGetIndex() {

		given().when().get("/index.html").then().statusCode(Status.OK.getStatusCode());

	}

}
