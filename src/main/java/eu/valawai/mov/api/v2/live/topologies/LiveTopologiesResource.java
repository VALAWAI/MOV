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
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.live.components.GetLiveTopology;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
	 * @param offset the index of the first live topology component to return.
	 * @param limit  the maximum number of live topology components to return.
	 *
	 * @return the current live topology.
	 */
	@GET
	@Operation(description = "Obtain the current live topology.")
	@APIResponse(responseCode = "200", description = "The current type topology", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveTopology.class)) })
	@APIResponse(responseCode = "404", description = "If the limit of the offset is not valid.")
	public Uni<Response> getLiveTopology(
			@Parameter(description = "The index of the first live topology component to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of live topology components to return") @QueryParam("limit") @DefaultValue("100") @Valid @Min(1) final int limit) {

		return GetLiveTopology.fresh().withOffset(offset).withLimit(limit).execute().onItem().ifNull()
				.continueWith(() -> new LiveTopology()).map(topology -> Response.ok(topology).build());

	}

}
