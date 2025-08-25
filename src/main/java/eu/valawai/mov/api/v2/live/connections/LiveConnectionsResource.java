/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.live.topology.GetLiveConnectionPage;
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
 * The web services to obtain information of the active connections.
 *
 * @author VALAWAI
 */
@Path("/v2/live/connections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LiveConnectionsResource {

	/**
	 * Get the current {@link LiveConnection}.
	 *
	 * @param offset the index of the first live connection component to return.
	 * @param limit  the maximum number of live connection components to return.
	 *
	 * @return the current live connection.
	 */
	@GET
	@Operation(description = "Obtain the current live connection.")
	@APIResponse(responseCode = "200", description = "The current type connection", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveConnectionPage.class)) })
	@APIResponse(responseCode = "404", description = "If the limit of the offset is not valid.")
	public Uni<Response> getLiveConnection(
			@Parameter(description = "The index of the first live connection component to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of live connection components to return") @QueryParam("limit") @DefaultValue("100") @Valid @Min(1) final int limit) {

		return GetLiveConnectionPage.fresh().withOffset(offset).withLimit(limit).execute()
				.map(connection -> Response.ok(connection).build());

	}

}
