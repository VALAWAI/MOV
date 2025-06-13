/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.persistence.design.component.GetComponentDefinitionPage;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The resource that can be used to manage the component definitions.
 *
 * @see ComponentDefinition
 *
 * @author VALAWAI
 */
@Path("/v2/design/components")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComponentsResource {

	/**
	 * Get the information of some components.
	 *
	 * @param pattern to match the components name or description.
	 * @param type    to match the components.
	 * @param order   to return the components.
	 * @param offset  to the first component to return.
	 * @param limit   number maximum of components to return.
	 *
	 * @return the matching components page.
	 */
	@GET
	@Operation(description = "Obtain some components.")
	@APIResponse(responseCode = "200", description = "The page with the matching components", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ComponentDefinitionPage.class)) })
	@APIResponse(responseCode = "404", description = "When a parameter is not valid.")
	public Uni<Response> getMinComponentPage(
			@Parameter(description = "The pattern to match the name or description of the components to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("pattern") @Valid final String pattern,
			@Parameter(description = "The type to match the components to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("type") @Valid final String type,
			@Parameter(description = "The order in witch the components has to be returned. It is form by the field names, separated by a comma, and each of it with the - prefix for descending order or + for ascending.") @QueryParam("order") @DefaultValue("+since") @Valid @Pattern(regexp = "(,?[+|-]?[type|name|description|version|apiVersion])*") final String order,
			@Parameter(description = "The index of the first component to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of components to return") @QueryParam("limit") @DefaultValue("20") @Valid @Min(1) final int limit) {

		return GetComponentDefinitionPage.fresh().withPattern(pattern).withType(type).withOrder(order)
				.withOffset(offset).withLimit(limit).execute().map(page -> Response.ok(page).build());

	}

}
