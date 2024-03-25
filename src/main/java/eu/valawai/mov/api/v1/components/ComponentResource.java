/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import eu.valawai.mov.events.components.UnregisterComponentPayload;
import eu.valawai.mov.persistence.components.GetComponent;
import eu.valawai.mov.persistence.components.GetMinComponentPage;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Web services to manage the {@link Component}s.
 *
 * @see Component
 *
 * @author VALAWAI
 */
@Path("/v1/components")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComponentResource {

	/**
	 * The component to send the message to unregister a component.
	 */
	@Inject
	@Channel("send_unregister_component")
	Emitter<UnregisterComponentPayload> unregister;

	/**
	 * Get the information of some components.
	 *
	 * @param pattern             to match the components message.
	 * @param type                to match the components.
	 * @param hasPublishChannel   is {@code true} if the component must have at
	 *                            least one publish channel.
	 * @param hasSubscribeChannel is {@code true} if the component must have at
	 *                            least one subscribe channel.
	 * @param order               to return the components.
	 * @param offset              to the first component to return.
	 * @param limit               number maximum of components to return.
	 *
	 * @return the matching components page.
	 */
	@GET
	@Operation(description = "Obtain some components.")
	@APIResponse(responseCode = "200", description = "The page with the matching components", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MinComponentPage.class)) })
	@APIResponse(responseCode = "404", description = "When a parameter is not valid.")
	public Uni<Response> getMinComponentPage(
			@Parameter(description = "The pattern to match the name or description of the components to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("pattern") @Valid final String pattern,
			@Parameter(description = "The type to match the components to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("type") @Valid final String type,
			@Parameter(description = "This is true if the component must have at least one publish channel.") @QueryParam("hasPublishChannel") @DefaultValue("false") final boolean hasPublishChannel,
			@Parameter(description = "This is true if the component must have at least one subscribe channel.") @QueryParam("hasSubscribeChannel") @DefaultValue("false") final boolean hasSubscribeChannel,
			@Parameter(description = "The order in witch the components has to be returned. It is form by the field names, separated by a comma, and each of it with the - prefix for descending order or + for ascending.") @QueryParam("order") @DefaultValue("+since") @Valid @Pattern(regexp = "(,?[+|-]?[type|name|description|since])*") final String order,
			@Parameter(description = "The index of the first component to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of components to return") @QueryParam("limit") @DefaultValue("20") @Valid @Min(1) final int limit) {

		return GetMinComponentPage.fresh().withPattern(pattern).withType(type)
				.withAtLeastOnePublishChannel(hasPublishChannel).withAtLeastOneSubscribeChannel(hasSubscribeChannel)
				.withOrder(order).withOffset(offset).withLimit(limit).execute().map(page -> Response.ok(page).build());

	}

	/**
	 * Get the information of a components.
	 *
	 * @param componentId identifier of the component to get.
	 *
	 * @return the found component.
	 */
	@GET
	@Path("/{componentId:[0-9a-fA-F]{24}}")
	@Operation(description = "Obtain a component.")
	@APIResponse(responseCode = "200", description = "The component with the identifier", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MinComponentPage.class)) })
	@APIResponse(responseCode = "404", description = "When the component is not found.")
	public Uni<Response> getComponent(
			@Parameter(description = "Identifier of the component to get.", example = "000000000000000000000000", schema = @Schema(implementation = String.class)) @PathParam("componentId") final ObjectId componentId) {

		return GetComponent.fresh().withComponent(componentId).execute().map(model -> {

			if (model == null) {

				return Response.status(Status.NOT_FOUND).build();

			} else {

				return Response.ok(model).build();

			}
		});

	}

	/**
	 * Unregister a component.
	 *
	 * @param componentId identifier of the component to unregister.
	 *
	 * @return empty response if started to unregister or an error that explains why
	 *         can not be unregistered.
	 */
	@DELETE
	@Path("/{componentId:[0-9a-fA-F]{24}}")
	@Operation(description = "Unregister a component.")
	@APIResponse(responseCode = "204", description = "When the component started to unregister")
	@APIResponse(responseCode = "404", description = "When the component is not found.")
	public Uni<Response> unregisterComponent(
			@Parameter(description = "Identifier of the component to unregister.", example = "000000000000000000000000", schema = @Schema(implementation = String.class)) @PathParam("componentId") final ObjectId componentId) {

		final var payload = new UnregisterComponentPayload();
		payload.componentId = componentId;
		this.unregister.send(payload);
		return Uni.createFrom().item(Response.noContent().build());

	}

}
