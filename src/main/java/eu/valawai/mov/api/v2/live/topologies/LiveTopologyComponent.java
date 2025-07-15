/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.topologies;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.v1.components.MinComponent;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;

/**
 * A node that form part of a{@link LiveTopology}.
 *
 * @see LiveTopology
 * @see ComponentEntity
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
@Schema(title = "A node that form part of a live topology")
public class LiveTopologyComponent extends MinComponent {

	/**
	 * The connections that exit from this node
	 */
	@Schema(title = "The connections that exit from this node.")
	public List<LiveTopologyComponentOutConnection> connections;

}
