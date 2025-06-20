/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.design.topology.GetMinTopologyPage;
import eu.valawai.mov.persistence.design.topology.GetTopology;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
 * The resource that can be used to manage the topologies.
 *
 * @see Topology
 *
 * @author VALAWAI
 */
@Path("/v2/design/topologies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TopologiesResource {

	/**
	 * Get the information of some topologies.
	 *
	 * @param pattern to match the topologies name or description.
	 * @param order   to return the topologies.
	 * @param offset  to the first topology to return.
	 * @param limit   number maximum of topologies to return.
	 *
	 * @return the matching topologies page.
	 */
	@GET
	@Operation(description = "Obtain some topologies.")
	@APIResponse(responseCode = "200", description = "The page with the matching topologies", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MinTopologyPage.class)) })
	@APIResponse(responseCode = "404", description = "When a parameter is not valid.")
	public Uni<Response> getMinTopologyPage(
			@Parameter(description = "The pattern to match the name or description of the topologies to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("pattern") @Valid final String pattern,
			@Parameter(description = "The order in witch the topologies has to be returned. It is form by the field names, separated by a comma, and each of it with the - prefix for descending order or + for ascending.") @QueryParam("order") @DefaultValue("+since") @Valid @Pattern(regexp = "(,?[+|-]?[type|name|description|version|apiVersion])*") final String order,
			@Parameter(description = "The index of the first topology to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of topologies to return") @QueryParam("limit") @DefaultValue("20") @Valid @Min(1) final int limit) {

		return GetMinTopologyPage.fresh().withPattern(pattern).withOrder(order).withOffset(offset).withLimit(limit)
				.execute().map(page -> Response.ok(page).build());

	}

	/**
	 * Get the information of a topology.
	 *
	 * @param topologyId identifier of the topology to get.
	 *
	 * @return the topology associated to the identifier.
	 */
	@Path("/{topologyId:[0-9a-fA-F]{24}}")
	@GET
	@Operation(description = "Obtain some topologies.")
	@APIResponse(responseCode = "200", description = "The topology associated to the identifier", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MinTopologyPage.class)) })
	@APIResponse(responseCode = "400", description = "If not found a topology associated to the identifier.")
	public Uni<Response> getTopology(
			@Parameter(description = "The identifier of the topology to return.") @PathParam("topologyId") final @Valid @NotNull ObjectId topologyId) {

		return GetTopology.fresh().withId(topologyId).execute().map(topology -> {

			if (topology == null) {

				return Response.status(Response.Status.NOT_FOUND)
						.entity("Not found a topology associated to the path identifier.").build();

			} else {

				return Response.ok(topology).build();
			}

		});

	}

}
