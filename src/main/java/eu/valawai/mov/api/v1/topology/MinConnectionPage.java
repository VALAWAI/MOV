/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page with some connections.
 *
 * @author VALAWAI
 */
@Schema(title = "A page with some connections.")
public class MinConnectionPage extends Model {

	/**
	 * The number of connections that satisfy the query.
	 */
	@Schema(title = "The total number of connections that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(title = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The connections that match the query.
	 */
	@Schema(title = "The connections that satisfy the query")
	public List<MinConnection> connections;

}
