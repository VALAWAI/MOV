/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, OnDestroy, OnInit, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MainService } from '@app/main/main.service';
import { MessagesService } from '@app/shared/messages';
import { TopologyGraphElement } from '@app/shared/mov-api';
import { PointExtensions } from '@foblex/2d';
import { FCanvasComponent, FFlowModule } from '@foblex/flow';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';

@Component({
	standalone: true,
	selector: 'app-topology-editor',
	imports: [
		CommonModule,
		FFlowModule,
		MatButtonModule,
		MatTooltipModule,
		MatIconModule
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css'
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
	 * The height of the component.
	 */
	public height = 100;

	/**
	 * The wodth of the component.
	 */
	public width = 100;


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private messages: MessagesService
	) {

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
	 * Called whne teh scale has changed.
	 */
	public rescale(factor: number) {

		const canvas = this.fCanvas();
		var scale = factor * canvas.getScale();
		canvas.setScale(scale, PointExtensions.initialize());
		canvas.redrawWithAnimation()
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


}
