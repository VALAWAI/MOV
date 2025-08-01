/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	HostListener,
	inject,
	OnDestroy,
	OnInit,
	viewChild
} from '@angular/core';
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
	FCreateConnectionEvent,
	FZoomDirective,
	FReassignConnectionEvent
} from '@foblex/flow';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ConfigService, ToCssVariablePipe } from '@app/shared';
import { Observable, switchMap, of, Subscription, timer } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { TopologyNodeFormComponent } from './node-form.component';
import { TopologyConnectionFormComponent } from './connection-form.component';
import {
	MovApiService,
	Topology,
	ComponentType,
	ChannelSchema,
	matchPayloadSchema,
	ComponentDefinition
} from '@app/shared/mov-api';
import { IPoint, PointExtensions } from '@foblex/2d';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmSaveBeforeChangeDialog } from './confirm-save-before-change.dialog';
import { SelectTopologyToOpenDialog } from './select-topology-to-open.dialog';
import { ActivatedRoute } from '@angular/router';
import { DagreLayoutService, GraphModule } from '@app/shared/graph';
import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from '@angular/router';
import { EditorNode } from './editor-node.model';
import { EditorConnection } from './editor-connection.model';
import { EditorModule } from './editor.module';
import { TopologyEditorService } from './topology.service';
import {
	AddNodeAction,
	ChangeNodePositionAction,
	ChangeTopologyAction,
	CompositeAction,
	RemoveConnectionAction,
	ChangeConnectionTargetAction,
	AddConnectionAction
} from './actions';
import { TopologyFormComponent } from './topology-form.component';
import { EditorEndpoint } from './editor-endpoint.model';
import { SelectChannelDialog } from './select-channel.dialog';


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
		TopologyNodeFormComponent,
		TopologyConnectionFormComponent,
		MatDialogModule,
		TopologyFormComponent,
		GraphModule,
		EditorModule,
		ToCssVariablePipe
	],
	templateUrl: './editor.component.html',
	styleUrls: ['./editor.component.css'],
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
	public readonly conf = inject(ConfigService);

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
	 * Selected element.
	 */
	public selected: EditorNode | EditorConnection | null = null;

	/**
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * The height of the component.
	 */
	public width = 100;

	/**
	 * The active router.
	 */
	private route = inject(ActivatedRoute);

	/**
	 * The subscription to the query params.
	 */
	private subscriptions: Subscription[] = [];

	/**
	 * The layout manager using dagre.
	 */
	private readonly dagre = inject(DagreLayoutService);

	/**
	 * The component that do zooms.
	 */
	private fZoomDirective = viewChild.required(FZoomDirective);

	/**
	 * The service of teh editor.
	 */
	public readonly topology = inject(TopologyEditorService);

	/**
	 * Called when the window is resized.
	 */
	@HostListener('window:resize') windowResized() {

		this.height = Math.max(200, window.innerHeight - 125);
		this.width = window.innerWidth;
		if (this.width > 640) {

			this.width -= 340;
		}
		const canvas = this.fCanvas();
		canvas.redrawWithAnimation()
	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the topology editor@@main_topology_editor_code_page-title:Topology Editor`);
		this.windowResized();

		this.subscriptions.push(this.route.queryParams.pipe(
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
		));

		this.subscriptions.push(
			timer(this.conf.editorAutosaveTime, this.conf.editorAutosaveTime).subscribe(
				{
					next: () => {

						if (this.topology.unsaved && !this.topology.isEmpty) {

							this.saveTopology();
						}
					}
				}
			)
		);

		this.subscriptions.push(
			this.topology.changed$.subscribe(
				{
					next: action => {

						if (action.type == 'ADDED_NODE') {

							this.fFlow().select([action.id!], [], true);
							this.selected = this.topology.getNodeWith(action.id);

						} else if (action.type == 'ADDED_CONNECTION') {

							this.fFlow().select([], [action.id!], true);
							this.selected = this.topology.getConnectionWith(action.id);

						} else if (
							action.type == 'CHANGED_TOPOLOGY'
							|| (
								(action.type == 'REMOVED_NODE' || action.type == 'REMOVED_CONNECTION')
								&& action.id === this.selected?.id
							)
						) {

							this.fFlow().select([], [], true);
							this.selected = null;
						}

						this.updatedGraph();

					}
				}
			));
	}

	/**
	 * Unsubscribe from the subscriptions.
	 */
	public ngOnDestroy() {

		for (var subscription of this.subscriptions) {

			subscription.unsubscribe();
		}

		this.subscriptions = [];
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

		this.fZoomDirective().zoomIn();
	}

	/**
	 * Do a zoom out out the graph.
	 */
	public zoomOut() {

		this.fZoomDirective().zoomOut();
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
	public onSelectionChange(event: FSelectionChangeEvent) {

		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];
			this.selected = this.topology.nodes.find(n => n.id === selectedNodeId) || null;

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			this.selected = this.topology.connections.find(c => c.id === selectedConnectionId) || null;

		} else {

			this.selected = null;
		}
		this.updatedGraph();

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
	 * Called when has to change the topology of the editor.
	 */
	public changeTopology(newTopology: Topology = new Topology()) {

		if (this.topology.unsaved && !this.topology.isEmpty) {

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

			var action = new ChangeTopologyAction(newTopology);
			this.topology.apply(action);
			if (newTopology.id != null) {

				this.topology.stored(newTopology.id);
			}
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
		if (model.name == null || model.name.trim().length == 0) {
			// a name is required to allow to store the topology
			model.name = $localize`:The name to set to the topology when any is defined@@main_topology_editor_code_default-unamed-topology-name:Unnamed`;
		}
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

						this.topology.stored(stored.id);
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
	 * Called when a connection is added into the graf.
	 */
	public onConnectionAdded(event: FCreateConnectionEvent): void {

		this.searchPosibleConnection(event.fOutputId, event.fInputId, event.fDropPosition).subscribe(
			{
				next: possible => {

					if (possible != null) {

						if (possible.source == null || possible.target == null) {

							this.messages.showError(
								$localize`:Error message when not exist an endpoint to link a new connection@@main_topology_editor_code_add-connection-target-endpoint-undefined-error-msg:Does not exist a channel where the conection can be connected.`
							);

						} else {

							var connection = this.topology.getConnectionBetween(possible.source, possible.target);
							if (connection != null) {
								// Connection defined
								this.messages.showError(
									$localize`:Error message when try to add a conection that is already defined@@main_topology_editor_code_add-connection-duplicated-error-msg:Already exist a connection between this endpoionts.`
								);

							} else {

								connection = new EditorConnection(this.topology.nextConnectionId, possible.source, possible.target, possible.source.channel == null);
								var action = new AddConnectionAction(connection);
								this.topology.apply(action);
							}

						}

					}// else cancelled by the user
				}
			}
		);
	}

	/**
	 * Search for the posible endpoint for a connection.
	 */
	private searchPosibleConnection(sourceEndpointId: string, targetEndpointId: string | null | undefined, targetPoint: IPoint): Observable<PosibleConnection | null> {

		var sourceEndpoint = this.topology.getEndpointWith(sourceEndpointId);
		var targetEndpoint: EditorEndpoint | null = null;
		if (targetEndpointId != null) {

			targetEndpoint = this.topology.getEndpointWith(targetEndpointId);

		} else {

			var source = this.topology.getNodeWith(sourceEndpoint?.nodeId);
			var sourceChannel: ChannelSchema | null = null;
			if (source != null) {

				if (source.sourceNotification != null) {
					// add notification connection
					var notificationSource = this.topology.getNodeWith(source.sourceNotification.nodeId)!;
					sourceChannel = notificationSource.getChannelSchemaFor(source.sourceNotification.channel!);

				} else if (sourceEndpoint!.channel != null) {
					// add a new connection
					sourceChannel = source.getChannelSchemaFor(sourceEndpoint!.channel);
				}
			}


			var sourcePayload = sourceChannel?.publish;
			var normalizedTargetPoint = this.fFlow().getPositionInFlow(targetPoint);
			TARGET: for (var target of this.topology.nodes) {

				if (target.isPointInside(normalizedTargetPoint)) {

					if (target.component != null && target.component.channels != null) {

						var possibleChannels: ChannelSchema[] = [];
						for (var targetChannel of target.component.channels) {

							if (targetChannel.subscribe != null) {

								if (sourcePayload != null && matchPayloadSchema(sourcePayload, targetChannel.subscribe)) {
									// match a channel => create an endpoint to the channel
									targetEndpoint = target.searchEndpointOrCreate(targetChannel.name, false);
									break TARGET;
								}
								possibleChannels.push(targetChannel);
							}
						}

						if (possibleChannels.length > 0) {

							if (possibleChannels.length == 1) {
								// exist only one posibility
								targetEndpoint = target.searchEndpointOrCreate(possibleChannels[0].name, false);
								break;

							} else {
								//ask the user about the channel to use.
								return this.dialog.open(SelectChannelDialog, { data: possibleChannels }).afterClosed().pipe(
									map(channel => {

										if (channel != null) {

											targetEndpoint = target.searchEndpointOrCreate(channel.name, false);
											return { source: sourceEndpoint, target: targetEndpoint };

										} else {

											return null;
										}
									})
								);
							}
						}
					}
				}
			}
		}

		return of({ source: sourceEndpoint, target: targetEndpoint });
	}

	/**
	 * Called when a connection is try to be reasigned.
	 */
	public onReassignConnection(event: FReassignConnectionEvent) {

		if (!event.newTargetId) {

			this.searchPosibleConnection(event.oldSourceId, null, event.dropPoint).subscribe(
				{
					next: possible => {

						if (possible != null && possible.source != null) {

							if (possible.target == null) {

								// remove connection
								var removeAction = new RemoveConnectionAction(event.connectionId);
								this.topology.apply(removeAction);

							} else {

								var connection = this.topology.getConnectionBetween(possible.source, possible.target);
								if (connection != null) {
									// Connection defined
									this.messages.showError(
										$localize`:Error message when try to redirect to a conection that is already defined@@main_topology_editor_code_redirect-connection-duplicated-error-msg:Already exist a connection between this endpoionts.`
									);

								} else {

									var redirectAction = new ChangeConnectionTargetAction(event.connectionId, possible.target);
									this.topology.apply(redirectAction);
								}

							}

						}// else cancelled by the user
					}
				}
			);


		} else if (event.oldTargetId != event.newTargetId) {
			// redirect connection
			var newTargetEndpoint = this.topology.getEndpointWith(event.newTargetId)!;
			var redirectAction = new ChangeConnectionTargetAction(event.connectionId, newTargetEndpoint);
			this.topology.apply(redirectAction);
		}

	}

	/**
	 * Called when the position of a node has changed.
	 */
	public onNodePositionChange(node: EditorNode, newPosition: IPoint) {

		var action = new ChangeNodePositionAction(node.id, newPosition);
		this.topology.apply(action);
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

							graph.addEdge(connection.source.nodeId, connection.target.nodeId);
						}
						if (graph.layout()) {

							updated = true;
							var actions = new CompositeAction();
							for (var node of this.topology.nodes) {

								var newPoint = graph.getPositionFor(node.id);
								if (newPoint != null) {

									var action = new ChangeNodePositionAction(node.id, newPoint);
									actions.add(action);
								}
							}

							this.topology.apply(actions);
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

	/**
	 * Return the selected node.
	 */
	public get selectedEditorNode(): EditorNode | null {

		if (this.selected && 'endpoints' in this.selected) {

			return this.selected as EditorNode;
		}

		return null;
	}

	/**
	 * Return the selected connection.
	 */
	public get selectedEditorConnection(): EditorConnection | null {

		if (this.selected && 'source' in this.selected) {

			return this.selected as EditorConnection;
		}

		return null;
	}

	/**
	 * Called when whant to create a node.
	 */
	public onCreateNode(event: FCreateNodeEvent) {

		var node = new EditorNode(this.topology.nextNodeId);
		node.position = { x: event.rect.x, y: event.rect.y };
		node.component = new ComponentDefinition();
		node.component.type = event.data;
		var action = new AddNodeAction(node);
		this.topology.apply(action);

	}

	/**
	 * Add a node by its type.
	 */
	public addNodeByType(type: ComponentType, delta: number = 7) {

		var newNode = new EditorNode(this.topology.nextNodeId);
		newNode.component = new ComponentDefinition();
		newNode.component.type = type;
		var collision = false;
		do {

			collision = false;
			var upCorner = { x: newNode.position.x - delta, y: newNode.position.y - delta };
			var lowCorner = { x: newNode.position.x + newNode.width + delta, y: newNode.position.y + newNode.height + delta };
			for (var node of this.topology.nodes) {

				if (node.isPointInside(upCorner) || node.isPointInside(lowCorner)) {
					collision = true;
					newNode.position = {
						x: lowCorner.x + node.width,
						y: lowCorner.x + node.height
					};
					break;
				}

			}

		} while (collision);

		var action = new AddNodeAction(newNode);
		this.topology.apply(action);
		this.fCanvas().centerGroupOrNode(newNode.id);
		this.updatedGraph();
	}

}

/**
 * The function to check if cna leave the editor.
 */
export const leaveEditorGuard: CanDeactivateFn<TopologyEditorComponent> = (component: TopologyEditorComponent, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState: RouterStateSnapshot) => {

	return component.topology.unsaved ? component.storeBeforeLeave() : of(true);
};


/**
 * The representaiton of a posible connection.
 */
class PosibleConnection {

	/**
	 * The posible source.
	 */
	public source: EditorEndpoint | null = null;

	/**
	 * The posible target.
	 */
	public target: EditorEndpoint | null = null;
}
