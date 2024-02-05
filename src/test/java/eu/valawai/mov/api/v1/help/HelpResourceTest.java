/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.help;

import static io.restassured.RestAssured.given;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.MovApiTestCase;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link HelpResource}.
 *
 * @see HelpResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class HelpResourceTest extends MovApiTestCase {

	/**
	 * Should not found an undefined experiment.
	 */
	@Test
	public void shouldGetInformationOfTheWebServices() {

		final var config = ConfigProvider.getConfig();
		final var model = new Info();
		model.version = config.getOptionalValue("quarkus.application.version", String.class).orElse("1.0");
		model.profile = config.getOptionalValue("quarkus.profile", String.class).orElse("test");
		final var received = given().when().get("/v1/help/info").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(Info.class);
		Assertions.assertEquals(model, received);

	}
}
