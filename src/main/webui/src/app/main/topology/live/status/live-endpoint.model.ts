/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * A part of the node where the connections depart or arrive.
 */
export class LiveEndpoint {

	/**
	 * The identifier of the endpoint.
	 */
	public id: string = '';

	/**
	 * This is true if the endpoint is the source of a connection. Othewise is the target.
	 */
	public isSource: boolean = false;

	/**
	 * The name of the endpoint.
	 */
	public get name(): string {

		var matches = this.id.match(/^valawai\/c(0|1|2)\/\w+\/(.+)$/)
		if (matches != null) {

			return matches[2];

		} else {

			return '';
		}

	}


}
