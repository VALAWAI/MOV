/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * Contain the information of a version.
 *
 * @author VALAWAI
 */
export class VersionInfo {

	/**
	 * the name of the version.
	 */
	public name: string | null = null;

	/**
	 * The epoch time, in seconds, when the version is set.
	 */
	public since: number | null = null;
}
