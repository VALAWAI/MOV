/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import * as dagre from "@dagrejs/dagre";
import { IPoint } from '@foblex/2d';
import { Observable, of } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class DagreLayoutService {


	/**
	 * Crate a new graph.
	 */
	public createGraph(): Observable<DagreGraph> {

		return of(new DagreGraph());

	}


}

/**
 * A graph to layout the nodes using the Dagre library.
 */
export class DagreGraph {

	/**
	 * The graph that is
	 */
	private graph = new dagre.graphlib.Graph({ directed: true, compound: true, multigraph: true });

	/**
	 * Create the default configuration with the specified dirction.
	 * Read more about it at: https://github.com/dagrejs/dagre/wiki#configuring-the-layout
	 */
	private configWithDirection(dir: string): dagre.GraphLabel {

		return {
			rankdir: dir,
			nodesep: 250,
			ranksep: 250,
			edgesep: 250,
			marginx: 150,
			marginy: 150
		} as dagre.GraphLabel;
	}


	/**
	 * Set the layout from top to bottom.
	 */
	public vertical() {

		this.graph.setGraph(this.configWithDirection('TB'));
	}

	/**
	 * Set the layout from left to right.
	 */
	public horizontal() {

		this.graph.setGraph(this.configWithDirection('LR'));
	}

	/**
	 * Add a node to the graoph.
	 */
	public addNode(id: string, width: number, height: number) {

		this.graph.setNode(id, { width: width, height: height })

	}

	/**
	 * Add an edge to the graoph.
	 */
	public addEdge(sourceId: string, targetId: string) {

		this.graph.setEdge(sourceId, targetId, {})

	}

	/**
	 * Apply the layout to the specified direction.
	 */
	public layout(): boolean {

		try {

			dagre.layout(this.graph);
			return true;

		} catch (err) {

			console.error(err);
			return false;
		}
	}

	/**
	 * Get the position of a node..
	 */
	public getPositionFor(nodeId: string): IPoint | null {


		var graphNode = this.graph.node(nodeId);
		if (graphNode != null) {

			return { x: graphNode.x, y: graphNode.y };

		} else {

			return null;
		}
	}

}
