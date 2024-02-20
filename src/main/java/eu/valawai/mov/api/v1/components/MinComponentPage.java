/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page with some components.
 *
 * @author VALAWAI
 */
@Schema(title = "A page with some components.")
public class MinComponentPage extends Model {

	/**
	 * The number of components that satisfy the query.
	 */
	@Schema(title = "The total number of components that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(title = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The components that match the query.
	 */
	@Schema(title = "The components that satisfy the query")
	public List<MinComponent> components;

}
