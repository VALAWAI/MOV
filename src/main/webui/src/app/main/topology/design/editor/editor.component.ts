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
import { TopologyGraphElement } from '@app/shared/mov-api';
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
import { TopologyViewNodeModel } from './topolofy-view-node.model';



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
		FExternalItemDirective
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush
})
export class TopologyEditorComponent implements OnInit, OnDestroy {

	/**
	 * The selected component.
	 */
	public selected: TopologyGraphElement | null = null;

	/**
	 * The canvas with the hraph.
	 */
	protected fCanvas = viewChild.required(FCanvasComponent);

	/**
	 * The nodes of the graph.
	 */
	public nodes: TopologyViewNodeModel[] = [];

	/**
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * This is {@code true} if has to show the grid.
	 */
	public showGrid$: Observable<boolean>;

	public eConnectionBehaviour = EFConnectionBehavior;
	protected readonly eMarkerType = EFMarkerType;
	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private messages: MessagesService,
		private conf: ConfigService,
		private ref: ChangeDetectorRef
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



	}

	/**
	 * Called when a node is added to the graf.
	 */
	public onNodeAdded(event: FCreateNodeEvent): void {

		var node = new TopologyViewNodeModel();
		node.id = this.nodes.length.toString();
		node.type = event.data;
		node.position = { x: event.rect.x, y: event.rect.y };
		this.nodes.push(node);
		this.updatedGraph();
	}

	/**
	 * Called whne the graph has been updated.
	 */
	private updatedGraph() {

		this.ref.markForCheck();
		this.fCanvas().redrawWithAnimation();

	}

}
