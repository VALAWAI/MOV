/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

/**
 * This service is used to get raw data from the GitHub.
 *
 * @author VALAWAI
 */
@RegisterRestClient(configKey = "github-raw")
public interface GitHubRawService {

	/**
	 * Get the content of a file.
	 *
	 * @param organization where is defined the project.
	 * @param project      to get the file.
	 * @param branch       of the repository.
	 * @param path         to the file to get.
	 *
	 * @return the content of the file.
	 */
	@Path("/{organization}/{project}/refs/heads/{branch}/{path}")
	@Consumes(MediaType.TEXT_PLAIN)
	@GET
	public Uni<String> getRawFileStringContent(@PathParam("organization") String organization,
			@PathParam("project") String project, @PathParam("branch") String branch, @PathParam("path") String path);

	/**
	 * Return the {@code README.md} content of a VALAWAI component defrined in the
	 * repository.
	 *
	 * @param repository where the VALAWAI component is defined.
	 *
	 * @return the content of the {@code README.md}.
	 */
	public default Uni<String> getVALAWAIComponentReadmeContent(GitHubRepository repository) {

		return this.getRawFileStringContent("VALAWAI", repository.name, "main", "README.md");

	}

	/**
	 * Return the {@code asyncapi.yaml} content of a VALAWAI component defrined in
	 * the repository.
	 *
	 * @param repository where the VALAWAI component is defined.
	 *
	 * @return the content of the {@code asyncapi.yaml}.
	 */
	public default Uni<String> getVALAWAIComponentAsyncapiContent(GitHubRepository repository) {

		return this.getRawFileStringContent("VALAWAI", repository.name, "main", "asyncapi.yaml");

	}

}
