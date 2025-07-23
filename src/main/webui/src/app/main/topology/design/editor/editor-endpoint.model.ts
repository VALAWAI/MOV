/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * An endpoint where the nodes are used to connect the connections.
 */
export class EditorEndpoint {

	/**
	 * The identifvier of the endpoint.
	 */
	public id: string;

	/**
	 * The name of the endpoint.
	 */
	public name: string | null;


	/**
	 * Create the endppoint.
	 */
	constructor(
		public nodeId: string,
		public channel: string | null,
		public isSource: boolean
	) {

		this.name = null;
		this.id = nodeId;
		if (channel != null) {

			this.id += "_" + channel;
			var matches = channel.match(/^valawai\/c(0|1|2)\/\w+\/(.+)$/);
			if (matches != null) {

				this.name = matches[2];
			}

		} else {

			if (isSource) {

				this.id += "_output";

			} else {

				this.id += "_input";
			}

		}

	}

	/**
	 * Return the expected order of a 
	 */
	public compareTo(other: EditorEndpoint): number {

		if (this.name != null) {

			if (other.name == null) {

				return this.name.localeCompare('');

			} else {

				return this.name.localeCompare(other.name);
			}

		} else if (other.name != null) {

			return ''.localeCompare(other.name);

		} else {

			return 0;
		}

	}


}
