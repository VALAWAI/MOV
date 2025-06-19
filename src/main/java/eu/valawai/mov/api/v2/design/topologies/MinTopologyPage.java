/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page of the {@link MinTopology} that satisfy a query.
 *
 * @see MinTopology
 *
 * @author VALAWAI
 */
@Schema(description = "A page with some component definitions.")
public class MinTopologyPage extends Model {

	/**
	 * The number of topologies that satisfy the query.
	 */
	@Schema(description = "The total number of topologies that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(description = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The topologies that match the query.
	 */
	@Schema(description = "The topologies that satisfy the query")
	public List<MinTopology> topologies;

}
