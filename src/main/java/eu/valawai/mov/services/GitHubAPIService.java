/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/**
 * Service to interact with the GitHub.
 *
 * @author VALAWAI
 */
@RegisterRestClient(configKey = "github-api")
public interface GitHubAPIService {

	/**
	 * REturn the repositories defined in an organization.
	 *
	 * @param organization to get the repositories.
	 * @param limit        number maximum of repositories to return. It cannot be
	 *                     more than 100.
	 * @param pageOffset   the index of the first page to return. It starts per 1.
	 * @param type         Specifies the types of repositories you want returned.
	 *                     Can be one of: all, public, private, forks, sources,
	 *                     member
	 * @param sort         The property to sort the results by. Can be one of:
	 *                     created, updated, pushed, full_name.
	 * @param direction    The direction of the sort. Can be one of: asc or desc
	 *
	 * @return the repositories of the organization.
	 */
	@GET
	@Path("/orgs/{organization}/repos")
	public Uni<List<GitHubRepository>> getOrganizationRepositories(@PathParam("organization") String organization,
			@QueryParam("per_page") String limit, @QueryParam("page") String pageOffset,
			@QueryParam("type") String type, @QueryParam("sort") String sort,
			@QueryParam("direction") String direction);

	/**
	 * Return the repositories defined in the VALAWAI.
	 *
	 * @param limit      number maximum of repositories to return. It cannot be more
	 *                   than 100.
	 * @param pageOffset the index of the first page to return. It starts per 1.
	 *
	 * @return the repositories of the VALAWAI organization.
	 */
	public default Uni<List<GitHubRepository>> getVALAWAIRepositories(int limit, int pageOffset) {

		return this.getOrganizationRepositories("VALAWAI", String.valueOf(limit), String.valueOf(pageOffset), "public",
				"updated", "asc");

	}

}
