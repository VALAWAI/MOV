/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import eu.valawai.mov.api.Model;

/**
 * Contains the information about a GitHub repository.
 *
 * @author VALAWAI
 */
public class GitHubRepository extends Model {

	/**
	 * The identifier of the repository.
	 */
	public String id;

	/**
	 * The name of the repository.
	 */
	public String name;

}
