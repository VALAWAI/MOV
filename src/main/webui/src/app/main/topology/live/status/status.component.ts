/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, HostListener, inject, OnDestroy, OnInit, viewChild } from '@angular/core';
import { ConfigService } from '@app/shared';
import { PointExtensions } from '@foblex/2d';
import { FCanvasComponent, FFlowComponent, FFlowModule, FSelectionChangeEvent } from '@foblex/flow';
import { MainService } from 'src/app/main';
import { LiveNode } from './live-node.model';
import { GraphModule } from '@app/shared/graph/graph.module';
import { combineLatest, Subscription, switchMap, timer } from 'rxjs';
import { MessagesService } from '@app/shared/messages';
import { MinComponentPage, MinConnectionPage, MovApiService } from '@app/shared/mov-api';
import { DagreLayoutService } from '@app/shared/graph';
import { LiveConnection } from './live-connection.model';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';


/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-status',
	imports: [
		CommonModule,
		FFlowModule,
		GraphModule,
		MatIconModule,
		RouterModule,
		MatButtonModule
	],
	templateUrl: './status.component.html'
})
export class StatusComponent implements OnInit, OnDestroy {

	/**
	 *  The main service.
	 */
	private readonly header = inject(MainService);

	/**
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * The canvas with the hraph.
	 */
	protected fCanvas = viewChild.required(FCanvasComponent);

	/**
	 * The flow with the hraph.
	 */
	protected fFlow = viewChild.required(FFlowComponent);

	/**
	 * The configuration service.
	 */
	public readonly conf = inject(ConfigService);

	/**
	 * Service over the changes.
	 */
	private readonly ref = inject(ChangeDetectorRef);

	/**
	 * The nodes of the graph.
	 */
	public nodes: LiveNode[] = [];

	/**
	 * The connections of the graph.
	 */
	public connections: LiveConnection[] = [];

	/**
	 * The subscription to the polling process.
	 */
	private pullingSubscription: Subscription | null = null;

	/**
	 * Service to show user messages.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * Service to interact with the MOV.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The selected element.
	 */
	public selected: LiveNode | LiveConnection | null = null;

	/**
	 * The serv ice to layout the graph.
	 */
	private readonly dagre = inject(DagreLayoutService);

	/**
	 * Called when the window is resized.
	 */
	@HostListener('window:resize') windowResized() {

		this.height = window.innerHeight - 200;
		const canvas = this.fCanvas();
		canvas.redrawWithAnimation()
	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {


		this.header.changeHeaderTitle($localize`:The header title for the topology libe status@@main_topology_live_status_code_page-title:Live topology`);
		this.windowResized();

		this.pullingSubscription = timer(0, this.conf.pollingTime).pipe(
			switchMap(
				() => {
					return combineLatest([
						this.api.getMinComponentPage(null, null, null, null, null, 0, this.conf.liveMaxNodes),
						this.api.getMinConnectionPage(null, null, null, 0, this.conf.liveMaxEdges)]);
				}
			)
		).subscribe(
			{
				next: (pages) => {

					var changed = this.synchronizeNodes(pages[0], pages[1]);
					changed = this.synchronizeConnections(pages[1]) || changed;
					if (changed) {

						this.dagre.createGraph().subscribe(
							{
								next: graph => {

									graph.vertical();
									for (var node of this.nodes) {

										graph.addNode(node.id, node.width, node.height);
									}
									for (var connection of this.connections) {

										graph.addEdge(connection.sourceNodeId, connection.targetNodeId);
									}
									graph.layout();
									for (var node of this.nodes) {

										node.position = graph.getPositionFor(node.id) || node.position;

									}
									this.updatedGraph();
								}
							}
						);

					}
				},
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}

	/**
	 * Called to synchonide the live nodes with the information from the MOV.
	 */
	private synchronizeNodes(foundNodes: MinComponentPage, foundEdges: MinConnectionPage): boolean {

		var changed: boolean = false;
		if (foundNodes.components != null && foundNodes.components.length > 0) {

			COMPONENT: for (var component of foundNodes.components) {

				for (var node of this.nodes) {

					if (node.id === component.id) {

						changed = node.updateEndpointsWith(foundEdges) || changed;
						continue COMPONENT;
					}

				}

				var node = new LiveNode(component);
				node.updateEndpointsWith(foundEdges);
				this.nodes.push(node);
				changed = true;
			}

			NODE: for (var i = 0; i < this.nodes.length; i++) {

				var nodeId = this.nodes[i].id;
				for (var component of foundNodes.components) {

					if (component.id === nodeId) {

						continue NODE;
					}

				}

				this.nodes.splice(i, 1);
				i--;
				changed = true;
			}

		} else if (this.nodes.length > 0) {

			this.nodes = [];
			changed = true;

		}


		return changed;

	}

	/**
	 * Called to synchonide the live connections with the information from the MOV.
	 */
	private synchronizeConnections(page: MinConnectionPage): boolean {

		var changed = false;
		if (page.connections != null && page.connections.length > 0) {

			PAGE: for (var connection of page.connections) {

				for (var edge of this.connections) {

					if (edge.id === connection.id) {

						continue PAGE;
					}

				}

				var edge = new LiveConnection(connection);
				for (var node of this.nodes) {

					if (node.isNodeChannel(edge.sourceId)) {

						edge.sourceNodeId = node.id;
					}
					if (node.isNodeChannel(edge.targetId)) {

						edge.targetNodeId = node.id;
					}
				}
				this.connections.push(edge);
				changed = true;
			}

			CONNECTION: for (var i = 0; i < this.connections.length; i++) {

				var connectionId = this.connections[i].id;
				for (var connection of page.connections) {

					if (connection.id === connectionId) {

						continue CONNECTION;
					}

				}

				this.connections.splice(i, 1);
				i--;
				changed = true;
			}


		} else if (this.connections.length > 0) {

			this.connections = [];
			changed = true;

		}


		return changed;
	}

	/**
	 * Unsubscribe.
	 */
	public ngOnDestroy(): void {

		if (this.pullingSubscription != null) {

			this.pullingSubscription.unsubscribe();
			this.pullingSubscription = null;
		}
	}
	/**
	 * Called when the graph has been loaded.
	 */
	public fit() {

		const canvas = this.fCanvas();
		canvas.fitToScreen(PointExtensions.initialize(), true);

	}

	/**
	 * Do a zoom in in the graph.
	 */
	public zoomIn() {

		this.rescale(1.35)
	}

	/**
	 * Do a zoom out out the graph.
	 */
	public zoomOut() {

		this.rescale(0.65)
	}

	/**
	 * Called whne the scale has changed.
	 */
	private rescale(factor: number) {

		const canvas = this.fCanvas();
		var scale = factor * canvas.getScale();
		canvas.setScale(scale, PointExtensions.initialize());
		canvas.redrawWithAnimation()
	}

	/**
	 * Enable disable the grid.
	 */
	public toogleGrid() {

		this.conf.liveShowGrid = !this.conf.liveShowGrid;
		this.updatedGraph();
	}

	/**
	 * Called whne the graph has been updated.
	 */
	public updatedGraph() {

		this.ref.markForCheck();
		this.ref.detectChanges();
		this.fCanvas().redrawWithAnimation();

	}

	/**
	 * Called when something is selectd in the flow.
	 */
	public selectionChanged(event: FSelectionChangeEvent) {

		this.selected = null;
		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];
			for (var node of this.nodes) {

				if (node.id === selectedNodeId) {

					this.selected = node;
				}
			}

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			for (var edge of this.connections) {

				if (edge.id === selectedConnectionId) {

					this.selected = edge;
				}
			}
		}
		this.updatedGraph();
	}

	/**
	 * Check if the elemenbt is selected.
	 */
	public isSelected(value: LiveNode | LiveConnection | null | undefined) {

		if (value != null && this.selected != null) {

			return this.selected.id === value.id;

		} else {

			return false;
		}
	}

	/**
	 * Return the color for a defined conneciton in the topology.
	 */
	public colorFor(connection: LiveConnection): string {

		if (this.isSelected(connection)) {

			return 'var(--color-red-800)';

		} else if (connection.isEnabled) {

			return 'var(--color-sky-400)';

		} else {

			return 'var(--color-gray-200)';
		}
	}

	/**
	 * Return the selected node.
	 */
	public get selectedNode(): LiveNode | null {

		if (this.selected != null && 'component' in this.selected) {

			return this.selected as LiveNode;
		}

		return null;
	}

	/**
	 * Return the selected connection.
	 */
	public get selectedConnection(): LiveConnection | null {

		if (this.selected != null && 'connection' in this.selected) {

			return this.selected as LiveConnection;
		}

		return null;
	}

}
