/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * An endpoint where the nodes are used to connect the connections.
 */
export class StatusEndpoint {

	/**
	 * The identifvier of the endpoint.
	 */
	public id: string;


	/**
	 * Create the endppoint.
	 */
	constructor(
		public nodeId: string,
		public channel: string | null,
		public isSource: boolean
	) {

		this.id = nodeId;
		if (channel != null) {

			this.id += "_" + channel;

		} else if (isSource) {

			this.id += "_output";

		} else {

			this.id += "_input";


		}

	}

	/**
	 * Return the expected order of a 
	 */
	public compareTo(other: StatusEndpoint): number {

		if (this.channel != null) {

			if (other.channel == null) {

				return this.channel.localeCompare('');

			} else {

				return this.channel.localeCompare(other.channel);
			}

		} else if (other.channel != null) {

			return ''.localeCompare(other.channel);

		} else {

			return 0;
		}

	}


}
