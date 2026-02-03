/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.live.connections.GetLiveConnection;
import eu.valawai.mov.persistence.live.connections.GetLiveConnectionPage;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
	public Uni<Response> getLiveConnectionPage(
			@Parameter(description = "The index of the first live connection component to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of live connection components to return") @QueryParam("limit") @DefaultValue("100") @Valid @Min(1) final int limit) {

		return GetLiveConnectionPage.fresh().withOffset(offset).withLimit(limit).execute().onItem().ifNull()
				.continueWith(() -> new LiveConnectionPage()).map(connection -> Response.ok(connection).build());

	}

	/**
	 * Get a defined {@link LiveConnection}.
	 *
	 * @param connectionId the identifier of the connection to get.
	 *
	 * @return the current live connection associated to the identifier.
	 */
	@GET
	@Path("/{connectionId:[0-9a-fA-F]{24}}")
	@Operation(description = "Obtain a specific connection.")
	@APIResponse(responseCode = "200", description = "The connection associated to the identifier.", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveConnection.class)) })
	@APIResponse(responseCode = "404", description = "If any connection is associated to the identifier.")
	public Uni<Response> getLiveConnection(
			@Parameter(description = "Identifier of the component to get.", example = "000000000000000000000000", schema = @Schema(implementation = String.class)) @PathParam("connectionId") final ObjectId connectionId) {

		return GetLiveConnection.fresh().withConnection(connectionId).execute().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot get the live connection with identifier {0}", connectionId);
			return null;

		}).map(connection -> {

			if (connection == null) {

				return Response.status(Response.Status.NOT_FOUND).build();

			} else {

				return Response.ok(connection).build();
			}

		});

	}

}
