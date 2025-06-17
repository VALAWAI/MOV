/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GitHubRawService}.
 *
 * @see GitHubRawService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GitHubRawServiceTest extends MasterOfValawaiTestCase {

	/**
	 * The service to test.
	 */
	@RestClient
	public GitHubRawService service;

	/**
	 * Check that can get the repositories of an organization.
	 */
	@Test
	public void shouldNotGetRawFileStringContent() {

		this.assertFailure(this.service.getRawFileStringContent("", "", "", ""));

	}

	/**
	 * Check that can get the repositories of an organization.
	 */
	@Test
	public void shouldGetRawFileStringContent() {

		final var content = this
				.assertItemNotNull(this.service.getRawFileStringContent("github", ".github", "main", "README.md"));
		assertFalse(content.length() == 0);

	}

	/**
	 * Check that can get the {@code README.md} from a VALAWAI component.
	 */
	@Test
	public void shouldGetVALAWAIComponentReadmeContent() {

		final var repository = new GitHubRepository();
		repository.id = 781332782;
		repository.name = "C0_email_sensor";
		final var content = this.assertItemNotNull(this.service.getVALAWAIComponentReadmeContent(repository));
		assertFalse(content.length() == 0);

		assertTrue(content.indexOf("Summary") > 0);
		assertTrue(content.indexOf("IIIA-CSIC") > 0);

	}

	/**
	 * Check that can get the {@code README.md} from a VALAWAI component.
	 */
	@Test
	public void shouldGetVALAWAIComponentAsyncapiContent() {

		final var repository = new GitHubRepository();
		repository.id = 781332782;
		repository.name = "C0_email_sensor";
		final var content = this.assertItemNotNull(this.service.getVALAWAIComponentAsyncapiContent(repository));
		assertFalse(content.length() == 0);

		assertTrue(content.indexOf("C0 E-mail sensor") > 0);
		assertTrue(content.indexOf("valawai/c0/email_sensor/control/registered") > 0);

	}

}
