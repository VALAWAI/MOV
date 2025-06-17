/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import java.util.Date;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link GitHubRepository}.
 *
 * @see GitHubRepository
 *
 * @author VALAWAI
 */
public class GitHubRepositoryTest extends ModelTestCase<GitHubRepository> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GitHubRepository createEmptyModel() {

		return new GitHubRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(GitHubRepository model) {

		model.id = ValueGenerator.rnd().nextLong();
		model.name = ValueGenerator.nextPattern("Repository name {0}");
		model.description = ValueGenerator.nextPattern("Repository description {0}");
		model.full_name = ValueGenerator.nextPattern("Repository/full_name_{0}");
		model.html_url = ValueGenerator.nextPattern("https://github.com/VALAWAI/{0}");
		model.updated_at = new Date(ValueGenerator.nextPastTime()).toString();
	}

}
