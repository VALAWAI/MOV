/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.valawai.mov.api.v1.help;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The Web services to interact with the congenial team formation.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path("/v1/help")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HelpResource {

	/**
	 * The version of the application.
	 */
	@ConfigProperty(name = "quarkus.application.version", defaultValue = "1.0")
	String version;

	/**
	 * The version of the application.
	 */
	@ConfigProperty(name = "quarkus.application.name", defaultValue = "master_of_valawai")
	String name;

	/**
	 * The version of the application.
	 */
	@ConfigProperty(name = "quarkus.profile", defaultValue = "production")
	String profile;

	/**
	 * Return the information about the web services.
	 *
	 * @return the information of the web services.
	 */
	@GET
	@Path("/info")
	@Operation(description = "Provide information about the running API.")
	@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Info.class)))
	public Uni<Response> getInfo() {

		final var info = new Info();
		info.name = this.name;
		info.version = this.version;
		info.profile = this.profile;
		Log.tracev("GET /v1/help/info => OK with {1}", info);
		return Uni.createFrom().item(Response.ok(info).build());

	}

}
