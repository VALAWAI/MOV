/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.live.components.GetLiveTopology;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The resource to get the {@link LiveTopology}.
 *
 * @author VALAWAI
 */
@Path("/v2/live/topologies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LiveTopologiesResource {

	/**
	 * Get the current {@link LiveTopology}.
	 *
	 * @return the current live topology.
	 */
	@GET
	@Operation(description = "Obtain the current live topology.")
	@APIResponse(responseCode = "200", description = "The current type topology", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveTopology.class)) })
	public Uni<Response> getLiveTopology() {

		return GetLiveTopology.fresh().execute().map(topology -> Response.ok(topology).build());

	}

}
