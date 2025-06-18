/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, HostListener, OnDestroy, OnInit, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MainService } from '@app/main/main.service';
import { MessagesService } from '@app/shared/messages';
import { PointExtensions } from '@foblex/2d';
import {
	EFConnectionBehavior,
	EFMarkerType,
	FCanvasComponent,
	FExternalItemDirective,
	FFlowModule,
	FSelectionChangeEvent,
	FCreateNodeEvent
} from '@foblex/flow';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ConfigService } from '@app/shared';
import { Observable } from 'rxjs';
import { TopologyNodeEditorComponent } from './node-editor.component';
import { TopologyConnectionEditorComponent } from './connection-editor.component';
import {
	MovApiService,
	Topology,
	TopologyNode,
	DesignTopologyconnection,
	ComponentDefinition
} from '@app/shared/mov-api';


@Component({
	standalone: true,
	selector: 'app-topology-editor',
	imports: [
		CommonModule,
		FFlowModule,
		MatButtonModule,
		MatTooltipModule,
		MatIconModule,
		MatMenuModule,
		FExternalItemDirective,
		TopologyNodeEditorComponent,
		TopologyConnectionEditorComponent
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopologyEditorComponent implements OnInit, OnDestroy {

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
	public showGrid$: Observable<boolean>;

	/**
	 * Selected element.
	 */
	public selectedElement: TopologyNode | DesignTopologyconnection | null = null;

	/**
	 * The height of the component.
	 */
	public height = 100;


	public eConnectionBehaviour = EFConnectionBehavior;
	protected readonly eMarkerType = EFMarkerType;
	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private messages: MessagesService,
		private conf: ConfigService,
		private ref: ChangeDetectorRef,
		private api: MovApiService
	) {

		this.showGrid$ = this.conf.editorShowGrid$;
	}

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
	 * Add a C0 component.
	 */
	public addC0() {

	}

	/**
	 * Add a C1 component.
	 */
	public addC1() {

	}

	/**
	 * Add a C2 component.
	 */
	public addC2() {

	}

	/**
	 * Called when something is selectd in the flow.
	 */
	public selectionChanged(event: FSelectionChangeEvent) {

		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];
			for (let node of this.topology.nodes) {

				if (node.id == selectedNodeId) {

					this.selectedElement = node;
					break;
				}
			}

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];
			for (let connection of this.topology.connections) {

				if (connection.id == selectedConnectionId) {

					this.selectedElement = connection;
					break;
				}
			}

		} else {

			this.selectedElement = null;
		}

		this.ref.markForCheck();

	}

	/**
	 * Called when a node is added to the graf.
	 */
	public onNodeAdded(event: FCreateNodeEvent): void {

		var node = new TopologyNode();
		node.id = this.topology.nodes.length.toString();
		node.position = { x: event.rect.x, y: event.rect.y };
		node.component = new ComponentDefinition();
		node.component.type = event.data;
		this.topology.nodes.push(node);
		this.updatedGraph();
		this.selectedElement = node;
	}

	/**
	 * Called whne the graph has been updated.
	 */
	private updatedGraph() {

		this.ref.markForCheck();
		this.fCanvas().redrawWithAnimation();

	}

	/**
	 * Called whne has to delete a node.
	 */
	public deleteNode(node: TopologyNode) {

		for (let i = 0; i < this.topology.nodes.length; i++) {

			if (node.id == this.topology.nodes[i].id) {

				if (this.selectedElement?.id == node.id) {

					this.selectedElement = null;
				}
				this.topology.nodes.splice(i, 1);
				this.ref.markForCheck();
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

			if (node.id == this.topology.nodes[i].id) {

				this.selectedElement = node;
				this.topology.nodes.splice(i, 1,node);
				this.ref.markForCheck();
				return;
			}
		}

		this.topology.nodes.push(node);
	}

}
