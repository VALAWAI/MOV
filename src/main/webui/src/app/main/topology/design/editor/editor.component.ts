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
	DesignTopologyConnection,
	ComponentDefinition,
	ComponentType,
	Point,
	MinTopology
} from '@app/shared/mov-api';
import { IPoint, PointExtensions } from '@foblex/2d';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmSaveBeforeChangeDialog } from './confirm-save-before-change.dialog';
import { SelectTopologyToOpenDialog } from './select-topology-to-open.dialog';
import { MinTopologyEditorComponent } from './min-topology-editor.component';
import { ConnectionData, NodeData, TopologyData, TopologyElement } from './editor.models';


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
		MatDialogModule,
		MinTopologyEditorComponent
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
	 * The component to manage the dialogs.
	 */
	private readonly dialog = inject(MatDialog);

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
	public topology: TopologyData = new TopologyData();

	/**
	 * This is {@code true} if has to show the grid.
	 */
	public showGrid$: Observable<boolean> = this.conf.editorShowGrid$;

	/**
	 * Selected element.
	 */
	public selectedElement: TopologyElement | null = null;

	/**
	 * The height of the component.
	 */
	public height = 100;


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

		this.selectedElement = this.topology.getElementFor(event);

		// the graph is not changed only the selected value
		this.ref.detectChanges();
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

		this.selectedElement = this.topology.addNodeWithType(type, x, y);
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

		this.ref.detectChanges();
		this.fCanvas().redrawWithAnimation();

	}

	/**
	 * Called when has to delete a node.
	 */
	public deleteNode(node: NodeData) {

		if (this.topology.removeNode(node.id)) {

			if (this.selectedElement?.id == node.id) {

				this.selectedElement = null;
			}
			this.updatedGraph();
		}

	}

	/**
	 * Check if a node is selected.
	 */
	public isNodeSelected(): boolean {

		return this.selectedElement instanceof NodeData;
	}

	/**
	 * Return the selected node or {@code null} if not selected.
	 */
	public get selectedNode(): TopologyNode | null {

		if (this.isNodeSelected()) {

			return (this.selectedElement as NodeData).model;
		}

		return null;
	}

	/**
	 * Check if a connection is selected.
	 */
	public isConnectionSelected(): boolean {

		return this.selectedElement instanceof ConnectionData;
	}

	/**
	 * Return the selected connection or {@code null} if not selected.
	 */
	public get selectedConnection(): DesignTopologyConnection | null {

		if (this.isConnectionSelected()) {

			return (this.selectedElement as ConnectionData).model;
		}

		return null;
	}

	/**
	 * Called when the selected node has changed.
	 */
	public updatedSelectedNode(node: TopologyNode) {

		if (this.selectedElement != null
			&& this.topology.updateNodeModel(this.selectedElement.id, node)
		) {

			this.updatedGraph();
		}

	}

	/**
	 * Called when has to change the topology of the editor.
	 */
	public changeTopology(newTopology: Topology = new Topology()) {

		if (this.topology.modified) {

			this.dialog.open(ConfirmSaveBeforeChangeDialog).afterClosed().subscribe(
				{
					next: (result) => {

						if (result === true) {

							var model = this.topology.model;
							var action: Observable<any> = model.id != null ? this.api.updateDesignedTopology(model) : this.api.storeDesignedTopology(model);
							action.subscribe(
								{
									next: () => {
										this.topology.modified = false;
										this.changeTopology(newTopology);
									},
									error: err => this.messages.showMOVConnectionError(err)
								}
							);

						} else {

							this.topology.modified = false;
							this.changeTopology(newTopology);
						}
					}
				}

			);


		} else {

			this.topology = new TopologyData(newTopology);
			this.selectedElement = null;
			this.fit();
			this.updatedGraph();
			this.messages.showSuccess($localize`:Message to explain when success changed the topology@@main_topology_editor_code_changed-topology:Topology chnaged`);
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

		var model = this.topology.model;
		var action: Observable<Topology> = model.id != null ? this.api.updateDesignedTopology(model) : this.api.storeDesignedTopology(model);
		action.subscribe(
			{
				next: savedTopology => {

					this.topology.id = savedTopology.id;
					this.topology.modified = false;
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


	/**
	 * Called when has changed teh values of teh topology.
	 */
	public updatedTopology(min: MinTopology) {

		this.topology.min = min;
		this.topology.modified = true;
		this.ref.detectChanges();
	}

	/**
	 * Called whne the position of a node has changed.
	 */
	public updatedNodePosition(nodeId: string, point: IPoint) {

		if (this.topology.updateNodePosition(nodeId, point.x, point.y)) {

			this.ref.detectChanges();
		}
	}

	/**
	 * Called when the selected connection has changed.
	 */
	public updatedSelectedConnection(connection: DesignTopologyConnection) {

		if (this.selectedElement != null
			&& this.topology.updateConnectionModel(this.selectedElement.id, connection)
		) {

			this.updatedGraph();
		}

	}
}
