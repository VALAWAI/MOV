/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, HostListener, inject, OnDestroy, OnInit, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MainService } from '@app/main/main.service';
import { MessagesService } from '@app/shared/messages';
import {
	EFConnectionBehavior,
	EFMarkerType,
	FCanvasComponent,
	FExternalItemDirective,
	FFlowModule,
	FSelectionChangeEvent,
	FCreateNodeEvent,
	FFlowComponent
} from '@foblex/flow';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ConfigService } from '@app/shared';
import { Observable, of, switchMap } from 'rxjs';
import { TopologyNodeEditorComponent } from './node-editor.component';
import { TopologyConnectionEditorComponent } from './connection-editor.component';
import {
	MovApiService,
	Topology,
	TopologyNode,
	DesignTopologyconnection,
	ComponentDefinition,
	ComponentType,
	Point,
	MinTopology
} from '@app/shared/mov-api';
import { PointExtensions } from '@foblex/2d';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmSaveBeforeChangeDialog } from './confirm-save-before-change.dialog';
import { SelectTopologyToOpenDialog } from './select-topology-to-open.dialog';


@Component({
	standalone: true,
	selector: 'app-topology-editor',
	imports: [
		CommonModule,
		FFlowModule,
		MatButtonModule,
		MatIconModule,
		MatMenuModule,
		FExternalItemDirective,
		TopologyNodeEditorComponent,
		TopologyConnectionEditorComponent,
		MatDialogModule
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopologyEditorComponent implements OnInit, OnDestroy {

	/**
	 *  The service over teh main view. 
	 */
	private readonly header = inject(MainService);

	/**
	 * The service to show messages.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * The service to access the APP configuration.
	 */
	private readonly conf = inject(ConfigService);

	/**
	 * Service over the changes.
	 */
	private readonly ref = inject(ChangeDetectorRef);

	/**
	 * Service to access to teh MOV API.
	 */
	private readonly api = inject(MovApiService);


	/**
	 * The flow with the hraph.
	 */
	protected fFlow = viewChild.required(FFlowComponent);

	/**
	 * The canvas with the hraph.
	 */
	protected fCanvas = viewChild.required(FCanvasComponent);

	/**
	 * The topology that is editing.
	 */
	public topology: Topology = new Topology();

	/**
	 * This is {@code true} if has to show the grid.
	 */
	public showGrid$: Observable<boolean> = this.conf.editorShowGrid$;

	/**
	 * Selected element.
	 */
	public selectedElement: TopologyNode | DesignTopologyconnection | null = null;

	/**
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * This is {@code true} if the topology is not saved.
	 */
	public unsaved = false;


	/**
	 * The component to manage the dialogs.
	 */
	private readonly dialog = inject(MatDialog);


	public eConnectionBehaviour = EFConnectionBehavior;
	protected readonly eMarkerType = EFMarkerType;

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

		this.header.changeHeaderTitle($localize`:The header title for the topology editor@@main_topology_editor_code_page-title:Topology Editor`);
		this.windowResized();
	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {


	}

	/**
	 * Called when the graph has been loaded.
	 */
	public loaded() {

		this.messages.showSuccess("Loaded graph");

		this.fit();

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
	 * Called whne teh scale has changed.
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

		this.conf.editorShowGrid = !this.conf.editorShowGrid;
		this.fCanvas().redraw();
	}

	/**
	 * Called when something is selectd in the flow.
	 */
	public selectionChanged(event: FSelectionChangeEvent) {

		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];
			for (let node of this.topology.nodes) {

				if (node.tag == selectedNodeId) {

					this.selectedElement = node;
					break;
				}
			}

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			for (let connection of this.topology.connections) {

				if (connection.tag == selectedConnectionId) {

					this.selectedElement = connection;
					break;
				}
			}

		} else {

			this.selectedElement = null;
		}

		// the graph is not changed only the selected value
		this.ref.markForCheck();
	}

	/**
	 * Called when a node is added to the graf.
	 */
	public onNodeAdded(event: FCreateNodeEvent): void {

		this.addNode(event.data, event.rect.x, event.rect.y);
	}

	/**
	 * Add a node to the topology.
	 */
	private addNode(type: ComponentType, x: number, y: number) {

		var newNode = new TopologyNode();
		var id = this.topology.nodes.length + 1;
		newNode.tag = 'node_' + id;
		newNode.position = new Point();
		newNode.position.x = x;
		newNode.position.y = y;
		newNode.component = new ComponentDefinition();
		newNode.component.type = type;
		var collision = true;
		while (collision) {

			collision = false;
			for (var node of this.topology.nodes) {

				if (node.tag == newNode.tag) {

					id++;
					newNode.tag = 'node' + id;
					collision = true;
					break;

				} else {

					var distance = Point.distance(node.position, newNode.position);
					if (distance < 64) {

						newNode.position.x += 64;
						newNode.position.y += 64;
						collision = true;
						break;

					}
				}
			}

		}
		this.topology.nodes.push(newNode);
		this.selectedElement = newNode;
		this.updatedGraph();
	}

	/**
	 * Called to add a node by type..
	 */
	public addNodeByType(type: ComponentType): void {

		var x = 0;
		var y = 0;
		var box = this.fFlow().getNodesBoundingBox();
		if (box != null) {

			var x = (box.x + box.width) / 2.0;
			var y = (box.y + box.height) / 2.0;
		}
		this.addNode(type, x, y);

	}

	/**
	 * Called whne the graph has been updated.
	 */
	private updatedGraph() {

		this.unsaved = true;
		this.ref.markForCheck();
		this.fCanvas().redrawWithAnimation();

	}

	/**
	 * Called when has to delete a node.
	 */
	public deleteNode(node: TopologyNode) {

		for (let i = 0; i < this.topology.nodes.length; i++) {

			if (node.tag == this.topology.nodes[i].tag) {

				if (this.selectedElement?.tag == node.tag) {

					this.selectedElement = null;
				}
				this.topology.nodes.splice(i, 1);
				this.updatedGraph();
				return;
			}
		}


	}

	/**
	 * Return the selected node or {@code null} if not selected.
	 */
	public get selectedNode(): TopologyNode | null {

		if (this.selectedElement?.constructor?.name === 'TopologyNode' || this.selectedElement instanceof TopologyNode) {

			return this.selectedElement as TopologyNode;
		}

		return null;
	}

	/**
	 * Return the selected connection or {@code null} if not selected.
	 */
	public get selectedConnection(): DesignTopologyconnection | null {

		if (this.selectedElement?.constructor?.name === 'Topologyconnection' || this.selectedElement instanceof DesignTopologyconnection) {

			return this.selectedElement as DesignTopologyconnection;
		}

		return null;
	}

	/**
	 * Called when the node has changed.
	 */
	public updatedNode(node: TopologyNode) {

		for (let i = 0; i < this.topology.nodes.length; i++) {

			if (node.tag == this.topology.nodes[i].tag) {

				this.selectedElement = node;
				this.topology.nodes.splice(i, 1, node);
				this.updatedGraph();
				return;
			}
		}

		this.topology.nodes.push(node);
		this.updatedGraph();
	}

	/**
	 * Called when has to chnage the topology of the editor.
	 */
	public changeTopology(newTopology: Topology = new Topology()) {

		if (this.unsaved) {

			this.dialog.open(ConfirmSaveBeforeChangeDialog).afterClosed().subscribe(
				{
					next: (result) => {

						if (result === true) {

							var action: Observable<any> = this.topology.id != null ? this.api.updateDesignedTopology(this.topology) : this.api.storeDesignedTopology(this.topology);
							action.subscribe(
								{
									next: () => {
										this.unsaved = false;
										this.changeTopology(newTopology);
									},
									error: err => this.messages.showMOVConnectionError(err)
								}
							);

						} else {

							this.unsaved = false;
							this.changeTopology(newTopology);
						}
					}
				}

			);


		} else {

			this.topology = newTopology;
			this.selectedElement = null;
			this.fit();
		}
	}

	/**
	 * Select a topology to be opened.
	 */
	public openTopology() {

		this.dialog.open(SelectTopologyToOpenDialog).afterClosed().pipe(
			switchMap(
				minTopology => this.api.getTopologyFrom(minTopology)
			)
		).subscribe(
			{
				next: (result: Topology | null) => {

					if (result != null) {

						this.changeTopology(result);
					}
				}
			}
		);

	}

	/**
	 * Called when want to save the topology.
	 */
	public saveTopology() {

		var action: Observable<Topology> = this.topology.id != null ? this.api.updateDesignedTopology(this.topology) : this.api.storeDesignedTopology(this.topology);
		action.subscribe(
			{
				next: savedTopology => {

					this.topology.id = savedTopology.id;
					this.unsaved = false;
					this.messages.showSuccess(
						$localize`:Success message when the topology has bene saved@@main_topology_editor_code_save-success-msg:Topology saved!`
					);

				},
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}

	/**
	 * Called when want to duplicate the topology.
	 */
	public duplicateTopology() {

	}
}
