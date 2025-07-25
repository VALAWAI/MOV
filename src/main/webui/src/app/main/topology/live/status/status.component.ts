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
import { GraphModule } from '@app/shared/graph/graph.module';
import { Subscription, switchMap, timer, retry } from 'rxjs';
import { MessagesService } from '@app/shared/messages';
import { LiveTopology, LiveTopologyComponent, LiveTopologyComponentOutConnection, MovApiService } from '@app/shared/mov-api';
import { DagreLayoutService } from '@app/shared/graph';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { StatusNode } from './status-node.model';
import { StatusConnection } from './status-connection.model';
import { NotificationConnectionDetailComponent } from './notification-connection-detail.component';
import { ConnectionDetailComponent } from './connection-detail.component';
import { NodeDetailComponent } from './node-detail.component';
import { NotificationNodeDetailComponent } from './notification-node-detail.component';


export type SelectedType = 'COMPONENT' | 'NOTIFICATION' | 'CONNECTION' | 'NOTIFICATION_CONNECTION' | 'NONE';

/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-live-topology-status',
	imports: [
		CommonModule,
		FFlowModule,
		GraphModule,
		MatIconModule,
		RouterModule,
		MatButtonModule,
		NotificationConnectionDetailComponent,
		ConnectionDetailComponent,
		NodeDetailComponent,
		NotificationNodeDetailComponent
	],
	templateUrl: './status.component.html',
	styleUrls: ['./status.component.css']
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
	 * The live topology.
	 */
	private topology: LiveTopology = new LiveTopology();

	/**
	 * The nodes of the graph.
	 */
	public nodes: StatusNode[] = [];

	/**
	 * The connections of the graph.
	 */
	public connections: StatusConnection[] = [];

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
	public selected: StatusNode | StatusConnection | null = null;

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
			switchMap(() => this.api.getLiveTopology(0, this.conf.liveMaxNodes)),
			retry()
		).subscribe(
			{
				next: topology => {

					if (JSON.stringify(this.topology) != JSON.stringify(topology)) {
						// updated topology
						this.topology = topology;
						this.synchronizeGraphWithTopology();
					}
				}
			}
		);
	}


	/**
	 * Synchnoze the graph to match the topology.
	 */
	private synchronizeGraphWithTopology() {

		this.nodes = [];
		this.connections = [];
		if (this.topology.components != null) {

			for (var component of this.topology.components) {

				var node = this.searchOrCreateNode(component);
				node.updateWith(component);

				if (component.connections != null) {

					for (var outConnection of component.connections) {

						var sourceEndpoint = node.searchOrCreateEndpointFor(outConnection.channel, true);

						if (outConnection.notifications != null && outConnection.notifications.length > 0) {

							var notificationNode = this.searchOrCreateNode(outConnection!);

							var connectionToNotification = new StatusConnection(sourceEndpoint,
								notificationNode.searchOrCreateEndpointFor(null, false), outConnection);
							connectionToNotification.enabled = outConnection.target!.enabled;
							this.connections.push(connectionToNotification);

							sourceEndpoint = notificationNode.searchOrCreateEndpointFor(null, true);
							for (var notification of outConnection.notifications) {

								var targetNode = this.searchOrCreateNode(notification.id!);
								var targetEndpoint = targetNode.searchOrCreateEndpointFor(notification.channel!, false);
								var connection = new StatusConnection(sourceEndpoint, targetEndpoint, outConnection);
								connection.enabled = notification.enabled;
								this.connections.push(connection);
							}
						}

						var targetNode = this.searchOrCreateNode(outConnection.target!.id!);
						var targetEndpoint = targetNode.searchOrCreateEndpointFor(outConnection.target!.channel!, false);
						var connection = new StatusConnection(sourceEndpoint, targetEndpoint, outConnection);
						connection.enabled = outConnection.target!.enabled;
						this.connections.push(connection);
					}

					node.endpoints.sort((e1, e2) => e1.compareTo(e2));
				}
			}

		}

		setTimeout(() => this.updateLayoutGraph(), 1);

	}

	/**
	 * Search for a node of create it.
	 */
	private searchOrCreateNode(id: string | LiveTopologyComponent | LiveTopologyComponentOutConnection): StatusNode {

		var node = this.nodes.find(node => node.equalId(id)) || null;
		if (node == null) {

			node = new StatusNode(id);
			this.nodes.push(node);
			node.position = { x: 200 * this.nodes.length, y: 200 * this.nodes.length };
		}

		return node;

	}

	/**
	 * Apply layout iver the graph.
	 */
	private updateLayoutGraph() {

		this.dagre.createGraph().subscribe(
			{
				next: graph => {

					graph.vertical();
					for (var node of this.nodes) {

						graph.addNode(node.id, node.width, node.height);
					}
					for (var connection of this.connections) {

						graph.addEdge(connection.source.nodeId, connection.target.nodeId);
					}
					graph.layout();
					for (var node of this.nodes) {

						var newPosiiton = graph.getPositionFor(node.id);
						if (newPosiiton != null) {

							node.position = newPosiiton;
						}

					}
					this.fCanvas().resetScaleAndCenter(true);
					this.updatedGraph();
				}
			}
		);


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
			this.selected = this.nodes.find(node => node.id == selectedNodeId) || null;

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			this.selected = this.connections.find(connection => connection.id == selectedConnectionId) || null;
		}
		this.updatedGraph();
	}

	/**
	 * Check if the elemenbt is selected.
	 */
	public isSelected(value: StatusNode | StatusConnection | null | undefined) {

		if (value != null && this.selected != null) {

			return this.selected.id === value.id;

		} else {

			return false;
		}
	}

	/**
	 * Return the selected element type.
	 */
	public get selectedType(): SelectedType {

		if (this.selected != null) {

			if ('position' in this.selected) {

				if (this.selected.name != null) {

					return 'COMPONENT';

				} else {

					return 'NOTIFICATION';
				}

			} else if (this.selected.target.channel == this.selected.model.target!.channel
				|| this.selected.source.channel == this.selected.model.channel
			) {

				return 'CONNECTION';

			} else {

				return 'NOTIFICATION_CONNECTION';
			}

		}

		return 'NONE';
	}

	/**
	 * Obtain the sledcted status connection.
	 */
	public get selectedStatusConnection(): StatusConnection {

		return this.selected! as StatusConnection;
	}

	/**
	 * Obtain the sledcted status connection.
	 */
	public get selectedStatusNode(): StatusNode {

		return this.selected! as StatusNode;
	}

}
