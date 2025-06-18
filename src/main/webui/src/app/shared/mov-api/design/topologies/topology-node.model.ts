/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { ComponentDefinition } from "../components/component-definition.model";
import { Point } from "./point.model";

/**
 * Represents a single node within a {@link Topology} graph, defining its unique
 * identity, its graphical position, and the {@link ComponentDefinition} that it
 * embodies.
 *
 * @author VALAWAI
 */
export class TopologyNode {

	/**
	 * The unique identifier or tag for this node within the topology. This ID helps
	 * in establishing connections ({@link TopologyConnection}) to and from this
	 * specific node.
	 */
	public id: string = "";

	/**
	 * The graphical coordinates (X and Y) of the node within the topology
	 * visualization. This determines where the node is displayed on a canvas or
	 * diagram.
	 */
	public position: Point = new Point();

	/**
	 * A reference to the {@link ComponentDefinition} that this node represents.
	 * This defines the functional behavior and capabilities of the node.
	 */
	public component: ComponentDefinition | null = null;

}
