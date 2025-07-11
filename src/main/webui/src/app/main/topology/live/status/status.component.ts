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
import { Subscription, switchMap, timer } from 'rxjs';
import { MessagesService } from '@app/shared/messages';
import { MovApiService } from '@app/shared/mov-api';

/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-status',
	imports: [
		CommonModule,
		FFlowModule,
		GraphModule
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

					return this.api.getMinComponentPage(null, null, null, null, null, 0, 10000000)
				}
			)
		).subscribe(
			{
				next: () => {

				},
				error: err => this.messages.showMOVConnectionError(err)
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

		this.conf.editorShowGrid = !this.conf.editorShowGrid;
		this.fCanvas().redraw();
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

		if (event.fNodeIds.length > 0) {

			const selectedNodeId = event.fNodeIds[0];

		} else if (event.fConnectionIds.length > 0) {

			const selectedConnectionId = event.fConnectionIds[0];

		} else {

		}
	}

}
