/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * Represents the current status of the components library.
 *
 * @author VALAWAI
 */
@Schema(title = "Represents the current status of the components library.")
public class ComponentsLibraryStatus extends Model {

	/**
	 * The epoch timestamp, in seconds, of the oldest component found within the
	 * library.
	 */
	@Schema(description = "Epoch timestamp (seconds) of the oldest component in the library.", examples = "1678886400", defaultValue = "0")
	public long oldestComponentTimestamp = 0l;

	/**
	 * The epoch timestamp, in seconds, of the newest component found within the
	 * library.
	 */
	@Schema(description = "Epoch timestamp (seconds) of the newest component in the library.", examples = "1678886400", defaultValue = "0")
	public long newestComponentTimestamp = 0l;

	/**
	 * The total number of components currently available in the library.
	 */
	@Schema(title = "The total number of components currently available in the library.", examples = "23", defaultValue = "0")
	public long componentCount = 0;

}
