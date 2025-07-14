/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, inject, Input, Renderer2, viewChild } from '@angular/core';
import { EFConnectionType, EFMarkerType, FConnectionComponent, FFlowModule } from '@foblex/flow';

let edgeId = 0;

@Component({
	standalone: true,
	selector: 'app-graph-edge',
	imports: [
		CommonModule,
		FFlowModule
	],
	templateUrl: './graph-edge.component.html'
})
export class GraphEdgeComponent implements AfterViewInit {

	/**
	 * The posible types of markers.
	 */
	public eMarkerType = EFMarkerType;

	/**
	 * The type of connection.
	 */
	private fType: EFConnectionType = EFConnectionType.BEZIER;

	/**
	 * The color to fill the edge.
	 */
	@Input()
	public color: string = "fill-sky-400";

	/**
	 * The minimum offset for the edge..
	 */
	@Input()
	public offset: number = 30;

	/**
	 * The minimum offset for the edge..
	 */
	@Input()
	public id: string = `edge_${edgeId++}`;

	/**
	 * The identifier of the source.
	 */
	@Input()
	public sourceId: string = 'sourceId';

	/**
	 * The identifier of the target.
	 */
	@Input()
	public targetId: string = 'targetId';

	/**
	 * This is true if the edge is selected.
	 */
	@Input()
	public isSelected: boolean = false;


	/**
	 * The element of the component.
	 */
	private readonly element = inject(ElementRef);

	private readonly renderer = inject(Renderer2);

	/**
	 * The type of the connection.
	 */
	@Input()
	public set type(type: EFConnectionType | string | null | undefined) {

		this.fType = EFConnectionType.BEZIER;

		if (type) {

			var lower = type.toLowerCase().trim();
			if (lower == EFConnectionType.STRAIGHT) {

				this.fType = EFConnectionType.STRAIGHT;

			} else if (lower == EFConnectionType.SEGMENT) {

				this.fType = EFConnectionType.SEGMENT;
			}

		}
	}

	/**
	 * Return the type of the edge.
	 */
	public get type(): EFConnectionType {

		return this.fType;
	}

	/**
	 * 
	 * 
	 */
	public ngAfterViewInit() {

		var parent = this.element.nativeElement.parentNode;
		this.renderer.appendChild(parent, this.element.nativeElement.children[0]);
	}

}
