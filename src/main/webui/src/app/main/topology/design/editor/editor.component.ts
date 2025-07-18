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
	FCanvasComponent,
	FExternalItemDirective,
	FFlowModule,
	FSelectionChangeEvent,
	FCreateNodeEvent,
	FFlowComponent,
	FCreateConnectionEvent
} from '@foblex/flow';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ConfigService } from '@app/shared';
import { Observable, switchMap, of, Subscription } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { TopologyNodeEditorComponent } from './node-editor.component';
import { TopologyConnectionEditorComponent } from './connection-editor.component';
import {
	MovApiService,
	Topology,
	TopologyNode,
	DesignTopologyConnection,
	ComponentType,
	MinTopology
} from '@app/shared/mov-api';
import { IPoint, PointExtensions } from '@foblex/2d';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmSaveBeforeChangeDialog } from './confirm-save-before-change.dialog';
import { SelectTopologyToOpenDialog } from './select-topology-to-open.dialog';
import { MinTopologyEditorComponent } from './min-topology-editor.component';
import { ConnectionData, EndpointData, NodeData, TopologyData, TopologyElement } from './editor.models';
import { SelectNodeEndpointsDialog } from './select-node-endpoints.dialog';
import { ActivatedRoute } from '@angular/router';
import { DagreLayoutService, GraphModule } from '@app/shared/graph';
import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from '@angular/router';

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
		MinTopologyEditorComponent,
		GraphModule
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopologyEditorComponent implements OnInit, OnDestroy {

	/**
	 *  The service over the main view. 
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
	 * Service to access to the MOV API.
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
	private _selectedElement: TopologyElement | null = null;

	/**
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * This is true if the current topology is not saved.
	 */
	private unsaved: boolean = false;

	/**
	 * The active router.
	 */
	private route = inject(ActivatedRoute);

	/**
	 * The subscription to the chnageg of the query params.
	 */
	private queryParamSubsccriber: Subscription | null = null;

	/**
	 * The layout manager using dagre.
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

		this.header.changeHeaderTitle($localize`:The header title for the topology editor@@main_topology_editor_code_page-title:Topology Editor`);
		this.windowResized();

		this.queryParamSubsccriber = this.route.queryParams.pipe(
			switchMap(params => {

				var topologyId = params['topologyId'] || null;
				if (topologyId == null && this.conf.editorAutoloadLastTopology) {

					topologyId = this.conf.editorLastStoredTopologyId;
				}
				if (topologyId != null) {

					return this.api.getTopology(topologyId);

				} else {

					return of(null);
				}
			})

		).subscribe(
			{
				next: topology => {

					if (topology != null) {

						this.conf.editorLastStoredTopologyId = topology.id;
						this.changeTopology(topology);
					}
				},
				error: err => {
					this.messages.showMOVConnectionError(err);
					this.conf.editorLastStoredTopologyId = null;
				}
			}
		);
	}

	/**
	 * Unsubscribe from the subscriptions.
	 */
	public ngOnDestroy() {

		if (this.queryParamSubsccriber != null) {

			this.queryParamSubsccriber.unsubscribe();
			this.queryParamSubsccriber = null;
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

		this.conf.editorShowGrid = !this.conf.editorShowGrid;
		this.fCanvas().redraw();
	}

	/**
	 * Called when something is selectd in the flow.
	 */
	public selectionChanged(event: FSelectionChangeEvent) {

		this.selectedElement = this.topology.getElementFor(event);

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
		this.unsaved = true;
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

		this.ref.markForCheck();
		this.ref.detectChanges();
		this.fCanvas().redrawWithAnimation();

	}

	/**
	 * Called when has to delete a node.
	 */
	public deleteNode(node: NodeData) {

		if (this.topology.removeNode(node.id)) {

			this.unsaved = true;
			if (this.selectedElement?.id == node.id) {

				this.selectedElement = null;
			}
			this.updatedGraph();
		}

	}

	/**
	 * Change teh selected element.
	 */
	public set selectedElement(selected: TopologyElement | null) {

		this._selectedElement = selected;
		this.ref.markForCheck();
		this.ref.detectChanges();
	}

	/**
	 * Return  the selected element.
	 */
	public get selectedElement(): TopologyElement | null {

		return this._selectedElement;
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
	public get selectedNode(): NodeData | null {

		if (this.isNodeSelected()) {

			return (this.selectedElement as NodeData);
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
	public get selectedConnection(): ConnectionData | null {

		if (this.isConnectionSelected()) {

			return (this.selectedElement as ConnectionData);
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

			this.unsaved = true;
			this.updatedGraph();
		}

	}

	/**
	 * Called when has to change the topology of the editor.
	 */
	public changeTopology(newTopology: Topology = new Topology()) {

		if (this.unsaved) {

			this.dialog.open(ConfirmSaveBeforeChangeDialog).afterClosed()
				.pipe(
					switchMap(
						result => {

							if (result) {

								return this.storeModel();

							} else {

								return of(false);
							}
						}
					)
				)
				.subscribe(
					{
						next: (result) => {

							if (result === true) {

								this.changeTopology(newTopology);
							}
						}
					}

				);


		} else {

			this.topology = new TopologyData(newTopology);
			this.unsaved = this.topology.model.id == null;
			this.selectedElement = null;
			this.fit();
			this.updatedGraph();
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

						this.conf.editorLastStoredTopologyId = result.id;
						this.changeTopology(result);
					}
				}
			}
		);

	}

	/**
	 * Store the model.
	 */
	private storeModel(): Observable<boolean> {

		var model = this.topology.model;
		var action: Observable<any> = model.id != null ? this.api.updateDesignedTopology(model) : this.api.storeDesignedTopology(model);
		return action.pipe(
			catchError(
				err => {
					this.messages.showMOVConnectionError(err);
					return of(null);
				}
			),
			map(
				stored => {

					if (stored != null) {

						this.topology.id = stored.id;
						this.unsaved = false;
						this.conf.editorLastStoredTopologyId = stored.id;
						return true;

					} else {

						return false;
					}
				}
			)
		);


	}

	/**
	 * Called when want to save the topology.
	 */
	public saveTopology() {

		this.storeModel().subscribe(
			{
				next: stored => {

					if (stored) {
						this.messages.showSuccess(
							$localize`:Success message when the topology has been saved@@main_topology_editor_code_save-success-msg:Topology saved!`
						);
					}
				}
			}
		);

	}

	/**
	 * Called when want to duplicate the topology.
	 */
	public duplicateTopology() {

		var cloned = JSON.parse(JSON.stringify(this.topology.model));
		cloned.id = null;
		this.changeTopology(cloned);

	}

	/**
	 * Called when has changed the values of the topology.
	 */
	public updatedTopology(min: MinTopology) {

		this.topology.min = min;
		this.unsaved = true;
		this.ref.markForCheck();
		this.ref.detectChanges();
	}

	/**
	 * Called whne the position of a node has changed.
	 */
	public updatedNodePosition(nodeId: string, point: IPoint) {

		var updated = this.topology.updateNodePosition(nodeId, point);
		if (updated) {

			this.unsaved = true;
			var cloned = JSON.parse(JSON.stringify(updated.model));
			this.selectedElement = new NodeData(cloned);
		}

	}

	/**
	 * Called when the selected connection has changed.
	 */
	public updatedSelectedConnection(connection: DesignTopologyConnection) {

		if (this.selectedElement != null
			&& this.topology.updateConnectionModel(this.selectedElement.id, connection)
		) {

			this.unsaved = true;
			this.updatedGraph();
		}

	}

	/**
	 * Called when has to edit and endpoint into a node.
	 */
	public editEndPointTo(node: NodeData) {

		this.dialog.open(SelectNodeEndpointsDialog, { data: node }).afterClosed().subscribe(
			{
				next: (result: EndpointData[] | null) => {

					if (result != null && node.changeEndpoints(result)) {

						this.unsaved = true;
						this.updatedGraph();
					}
				}
			}
		);

	}

	/**
	 * Called when a connection is added into the graf.
	 */
	public onConnectionAdded(event: FCreateConnectionEvent): void {

		if (event.fInputId != null && event.fInputId != null) {

			this.topology.addConnectionBetween(event.fOutputId, event.fInputId);
			this.unsaved = true;
			this.updatedGraph();

		}

	}

	/**
	 * Check if an element is selected.
	 */
	public isSelected(element: TopologyElement | null | undefined): boolean {

		if (this._selectedElement != null && element != null) {

			return this._selectedElement.id == element.id;
		}
		return false;

	}

	/**
	 * Called when has to change the layout.
	 */
	public changeLayout(direction: 'horizontal' | 'vertical') {

		this.dagre.createGraph().subscribe(
			{
				next: graph => {

					var updated = false;
					try {

						if (direction == 'horizontal') {

							graph.horizontal();

						} else {

							graph.vertical();
						}
						for (var node of this.topology.nodes) {

							graph.addNode(node.id, node.width, node.height);
						}
						for (var connection of this.topology.connections) {

							var sourceId = connection.model.source?.nodeTag || '';
							var targetId = connection.model.target?.nodeTag || '';
							graph.addEdge(sourceId, targetId);
						}
						if (graph.layout()) {

							updated = true;
							for (var node of this.topology.nodes) {

								var point = graph.getPositionFor(node.id);
								if (point != null) {

									node.position = point;
								}
							}
						}

					} catch (err) {
						updated = false;
						console.error(err);
					}

					if (!updated) {

						this.messages.showError(
							$localize`:Error message when not apply the layout@@main_topology_editor_code_layout-error-msg:Cannot apply the layout.`
						);
					}
				}
			}
		);

	}

	/**
	 * Check if has changes to be saved.
	 */
	public hasUnsavedChanges() {

		return this.unsaved;
	}

	/**
	 * Check if has changes to be saved.
	 */
	public storeBeforeLeave(): Observable<boolean> {

		return this.dialog.open(ConfirmSaveBeforeChangeDialog).afterClosed()
			.pipe(
				switchMap(
					result => {

						if (result) {

							return this.storeModel().pipe(
								tap(
									stored => {

										if (!stored) {

											this.messages.showError(
												$localize`:Error message when store before leave@@main_topology_editor_code_error-store-before-leave:Cannot store the topology.`
											);

										} else {

											this.messages.showSuccess(
												$localize`:Success message when store before leave@@main_topology_editor_code_sucees-store-before-leave:Topology has been saved.`
											);
										}
									}
								)
							);

						} else {

							return of(true);
						}
					}
				)
			);

	}

}

/**
 * The function to check if cna leave the editor.
  */
export const leaveEditorGuard: CanDeactivateFn<TopologyEditorComponent> = (component: TopologyEditorComponent, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState: RouterStateSnapshot) => {

	return component.hasUnsavedChanges()
		? component.storeBeforeLeave()
		: true;
};

