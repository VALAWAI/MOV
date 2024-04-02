/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import eu.valawai.mov.events.topology.ChangeTopologyPayload;
import eu.valawai.mov.events.topology.CreateConnectionPayload;
import eu.valawai.mov.events.topology.NodePayload;
import eu.valawai.mov.persistence.topology.GetMinConnectionPage;
import eu.valawai.mov.persistence.topology.GetTopologyConnection;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Web s service to manage the topology between components.
 *
 * @author VALAWAI
 */
@Path("/v1/topology")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Topology", description = "The services to manage the topology connections between the VALAWAI components")
public class TopologyResource {

	/**
	 * The component to send the message to create a topology connection.
	 */
	@Inject
	@Channel("send_create_connection")
	Emitter<CreateConnectionPayload> create;

	/**
	 * The component to send the message to create a topology connection.
	 */
	@Inject
	@Channel("send_change_topology")
	Emitter<ChangeTopologyPayload> change;

	/**
	 * Get the information of some connections.
	 *
	 * @param pattern   to match the source or target channel.
	 * @param component to match the source or target component.
	 * @param order     to return the connections.
	 * @param offset    to the first connection to return.
	 * @param limit     number maximum of connections to return.
	 *
	 * @return the matching connections page.
	 */
	@GET
	@Path("/connections")
	@Operation(description = "Obtain some connections.")
	@APIResponse(responseCode = "200", description = "The page with the matching connections", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MinConnectionPage.class)) })
	@APIResponse(responseCode = "404", description = "When a parameter is not valid.")
	public Uni<Response> getMinConnectionPage(
			@Parameter(description = "The pattern to match the source or target channel of the connections to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("pattern") @Valid final String pattern,
			@Parameter(description = "The component to match the source or target of the connections to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("component") @Valid final String component,
			@Parameter(description = "The order in witch the connections has to be returned. It is form by the field names, separated by a comma, and each of it with the - prefix for descending order or + for ascending.") @QueryParam("order") @DefaultValue("+source") @Valid @Pattern(regexp = "(,?[+|-]?[source|target|enabled|createTimestamp|updateTimestamp])*") final String order,
			@Parameter(description = "The index of the first connection to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of connections to return") @QueryParam("limit") @DefaultValue("20") @Valid @Min(1) final int limit) {

		return GetMinConnectionPage.fresh().withPattern(pattern).withComponent(component).withOrder(order)
				.withOffset(offset).withLimit(limit).execute().map(page -> Response.ok(page).build());

	}

	/**
	 * Get the information of a topology connections.
	 *
	 * @param connectionId identifier of the connection to get.
	 *
	 * @return the found topology connection.
	 */
	@GET
	@Path("/connections/{connectionId:[0-9a-fA-F]{24}}")
	@Operation(description = "Obtain a topology connection.")
	@APIResponse(responseCode = "200", description = "The topology connection with the identifier", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TopologyConnection.class)) })
	@APIResponse(responseCode = "404", description = "When the topology connection is not found.")
	public Uni<Response> getConnection(
			@Parameter(description = "Identifier of the topology connection to get.", example = "000000000000000000000000", schema = @Schema(implementation = String.class)) @PathParam("connectionId") final ObjectId connectionId) {

		return GetTopologyConnection.fresh().withConnection(connectionId).execute().map(model -> {

			if (model == null) {

				return Response.status(Status.NOT_FOUND).build();

			} else {

				return Response.ok(model).build();

			}
		});

	}

	/**
	 * Create a topology connection.
	 *
	 * @param connection to be created.
	 *
	 * @return empty response if started to create the connection or an error that
	 *         explains why can not be created.
	 */
	@POST
	@Path("/connections")
	@Operation(description = "Create a topology connection.")
	@APIResponse(responseCode = "204", description = "When the topology connection started to be created")
	@APIResponse(responseCode = "400", description = "When the topology connection is not valid.")
	public Uni<Response> createTopologyConnection(
			@RequestBody(description = "The connection to create", required = true, content = @Content(schema = @Schema(implementation = ConnectionToCreate.class))) @Valid ConnectionToCreate connection) {

		final var payload = new CreateConnectionPayload();
		payload.source = new NodePayload();
		payload.source.componentId = connection.sourceComponent;
		payload.source.channelName = connection.sourceChannel;
		payload.target = new NodePayload();
		payload.target.componentId = connection.targetComponent;
		payload.target.channelName = connection.targetChannel;
		payload.enabled = connection.enabled;
		this.create.send(payload);
		return Uni.createFrom().item(Response.noContent().build());

	}

	/**
	 * Change a topology connection.
	 *
	 * @param model with the change to do over the connection.
	 *
	 * @return empty response if started to modify the connection or an error that
	 *         explains why can not be changed.
	 */
	@PUT
	@Path("/connections/change")
	@Operation(description = "modify a topology connection.")
	@APIResponse(responseCode = "204", description = "When the topology connection started to be created")
	@APIResponse(responseCode = "400", description = "When the topology connection is not valid.")
	public Uni<Response> updateTopologyConnection(
			@RequestBody(description = "The changes to do over the connection", required = true, content = @Content(schema = @Schema(implementation = ChangeConnection.class))) @Valid ChangeConnection model) {

		final var payload = new ChangeTopologyPayload();
		payload.connectionId = model.connectionId;
		payload.action = model.action;
		this.change.send(payload);
		return Uni.createFrom().item(Response.noContent().build());

	}

}
