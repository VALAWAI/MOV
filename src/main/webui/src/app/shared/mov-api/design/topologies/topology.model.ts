/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { TopologyNode } from './topology-node.model';
import { TopologyConnection } from './topology-connection.model';


/**
 * Represents a logical topology definition of the components that define the
 * interaction between the VALAWAI component.
 *
 * @author VALAWAI
 */
export class Topology {

	/**
	 * The unique identifier of the topology. This is system-generated and read-only.
	 */
	public id: string | null = null;


	/**
	 * A unique, human-readable name for the topology. This field is mandatory.
	 */
	public name: string | null = null;

	/**
	 * An optional, detailed description of the topology's purpose or design.
	 */
	public description: string | null = null;

	/**
	 * A list of {@link TopologyNode} objects that represent the different VALAWAI
	 * components that form the value aware application.
	 */
	public nodes: TopologyNode[] = [];

	/**
	 * A list of {@link TopologyConnection} objects that define the possible
	 * interactions between the VALAWAI components.
	 */
	public connections: TopologyConnection[] = [];

}
