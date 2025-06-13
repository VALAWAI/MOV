/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GitHubAPIService}.
 *
 * @see GitHubAPIService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GitHubAPIServiceTest extends MasterOfValawaiTestCase {

	/**
	 * The service to test.
	 */
	@RestClient
	public GitHubAPIService service;

	/**
	 * Check that can get the repositories of an organization.
	 */
	@Test
	public void shouldNotGetOrganizationRepositories() {

		this.assertFailure(this.service.getOrganizationRepositories("", "10", "1", "public", "updated", "asc"));

	}

	/**
	 * Check that can get the repositories of an organization.
	 */
	@Test
	public void shouldGetOrganizationRepositories() {

		final var repos = this.assertItemNotNull(
				this.service.getOrganizationRepositories("github", "10", "1", "public", "updated", "asc"));
		assertFalse(repos.isEmpty());

	}

	/**
	 * Check that can get the information of the VALAWAI organization.
	 */
	@Test
	public void shouldGetVALAWAIRepositories() {

		final var repos = this.assertItemNotNull(this.service.getVALAWAIRepositories(10, 1));
		assertFalse(repos.isEmpty());

	}

}
