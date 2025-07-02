/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 * Represents the current status of the components library.
 *
 * @author VALAWAI
 */
export class ComponentsLibraryStatus {

	/**
	 * The epoch timestamp, in seconds, of the oldest component found within the library.
	 */
	public oldestComponentTimestamp: number = 0;

	/**
	 * The epoch timestamp, in seconds, of the newest component found within the library.
	 */
	public newestComponentTimestamp: number = 0;

	/**
	 * The total number of components currently available in the library.
	 */
	public componentCount: number = 0;


}

