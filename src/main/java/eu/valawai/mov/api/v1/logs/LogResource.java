/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import eu.valawai.mov.persistence.live.logs.GetLogRecordPage;
import io.quarkus.logging.Log;
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
 * Web services to manage the {@link Log}s.
 *
 * @see Log
 *
 * @author VALAWAI
 */
@Path("/v1/logs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Log", description = "The services to manage the log messages")
public class LogResource {

	/**
	 * Get the information of some logs.
	 *
	 * @param pattern          to match the logs message.
	 * @param level            to match the logs.
	 * @param componentPattern to match the name or description of the components
	 *                         associated to the logs message.
	 * @param componentType    to match the component type of the components
	 *                         associated to the logs message.
	 * @param order            to return the logs.
	 * @param offset           to the first log to return.
	 * @param limit            number maximum of logs to return.
	 *
	 * @return the matching logs page.
	 */
	@GET
	@Operation(description = "Obtain some logs.")
	@APIResponse(responseCode = "200", description = "The page with the matching logs", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LogRecordPage.class)) })
	@APIResponse(responseCode = "404", description = "When a parameter is not valid.")
	public Uni<Response> getLogRecordPage(
			@Parameter(description = "The pattern to match the message of the logs to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("pattern") @Valid final String pattern,
			@Parameter(description = "The level to match the logs to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("level") @Valid final String level,
			@Parameter(description = "The pattern to match the component name or description of the logs to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("componentPattern") @Valid final String componentPattern,
			@Parameter(description = "The type to match the component associated to the logs to return. If it is defined between / it is considered a PCRE regular expression.") @QueryParam("componentType") @Valid final String componentType,
			@Parameter(description = "The order in witch the logs has to be returned. It is form by the field names, separated by a comma, and each of it with the - prefix for descending order or + for ascending.") @QueryParam("order") @DefaultValue("+timestamp") @Valid @Pattern(regexp = "(,?[+|-]?[level|message|timestamp|component.name|component.description|component.type])*") final String order,
			@Parameter(description = "The index of the first log to return") @QueryParam("offset") @DefaultValue("0") @Valid @Min(0) final int offset,
			@Parameter(description = "The maximum number of logs to return") @QueryParam("limit") @DefaultValue("20") @Valid @Min(1) final int limit) {

		return GetLogRecordPage.fresh().withPattern(pattern).withLevel(level).withComponnetPattern(componentPattern)
				.withComponnetType(componentType).withOrder(order).withOffset(offset).withLimit(limit).execute()
				.map(page -> Response.ok(page).build());

	}

}
