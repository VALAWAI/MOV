/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * Contains information of a live topology
 *
 * @author VALAWAI
 */
@Schema(title = "The information of the live topology")
public class LiveTopology extends Model {

	/**
	 * The components that form part of the life topology.
	 */
	@Schema(title = "The components that form part of the life topology")
	public List<LiveTopologyComponent> components;

}
