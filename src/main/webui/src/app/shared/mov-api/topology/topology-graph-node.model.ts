/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { TopologyGraphElement } from './topology-graph-element.model';
import { IPoint } from '@foblex/2d';

/**
 * A graph element of the topology.
 *
 * @author VALAWAI
 */
export class TopologyGraphNode extends TopologyGraphElement {

	/**
	 * The identifier of the valaeai component associated to the element.
	 */
	public component: Component | null = null;

	/**
	 * The position of the node in the graph.
	 */
	public position: IPoint = { 'x': 0, 'y': 0 };

}
