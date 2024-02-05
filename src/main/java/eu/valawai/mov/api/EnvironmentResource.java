/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 *
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path("/env.js")
public class EnvironmentResource {

	/**
	 * The version of the application.
	 */
	@ConfigProperty(name = "mov.url", defaultValue = "http://${quarkus.http.host}:${quarkus.http.port}")
	String movUrl;

	/**
	 * Obtain the environment to use on the user interface.
	 *
	 * @return the environment file.
	 */
	@GET
	@Operation(description = "Provide information about the running API.")
	@APIResponse(responseCode = "200", content = @Content(mediaType = "text/javascript"))
	@Produces("text/javascript")
	public Uni<Response> getEnvironment() {

		final var env = new StringBuilder();
		env.append("(function(window) {\n");
		env.append("window[\"env\"] = window[\"env\"] || {};\n");
		env.append("window[\"env\"][\"movUrl\"] = \"");
		env.append(this.movUrl);
		env.append("\";\n");
		env.append("})(this);");
		return Uni.createFrom().item(Response.ok(env).build());
	}

}
