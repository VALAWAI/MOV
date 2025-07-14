/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { EFMarkerType, FFlowModule } from '@foblex/flow';


@Component({
	standalone: true,
	selector: 'app-connection-markers',
	imports: [
		CommonModule,
		FFlowModule
	],
	templateUrl: './connection-markers.component.html'
})
export class ConnectionMarkersComponent {

	/**
	 * The posible types of markers.
	 */
	public eMarkerType = EFMarkerType;

	/**
	 * The color to fill teh markers.
	 */
	@Input()
	public color: string = "fill-sky-400";

	/**
	 * The color to fill for the selected markers.
	 */
	@Input()
	public selectedColor: string = "fill-sky-800";


}
