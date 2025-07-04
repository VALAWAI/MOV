/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChangeDetectorRef, Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { EndpointData, NodeData } from "./editor.models";
import { MatListModule, MatSelectionListChange } from '@angular/material/list';
import { MessageComponent } from "@app/shared/messages";
import { MatIconModule } from "@angular/material/icon";
import { CommonModule } from "@angular/common";

@Component({
	standalone: true,
	selector: 'dialog-select-node-endpoints',
	templateUrl: 'select-node-endpoints.dialog.html',
	imports: [
		CommonModule,
		MatButtonModule,
		MatDialogModule,
		MatListModule,
		MessageComponent,
		MatIconModule
	],
})
export class SelectNodeEndpointsDialog {

	/**
	 * The controller for the selected topology.
	 */
	public data: NodeData = inject(MAT_DIALOG_DATA);

	/**
	 * The possible endpoints taht can be selected for the node.
	 */
	public endpoints: EndpointData[] = this.data.possibleEndpoint();

	/**
	 * The endpoints that has been selected.
	 */
	public selectedEndpoints: EndpointData[] = [...this.data.endpoints];


	/**
	 * Called when the selected endpoints has changed. 
	 */
	public endppointsSelectionChange(event: MatSelectionListChange) {

		for (var option of event.options) {

			if (option.selected) {

				this.selectedEndpoints.push(option.value);

			} else {

				for (var i = 0; i < this.selectedEndpoints.length; i++) {

					if (this.selectedEndpoints[i].id === option.value.id) {

						this.selectedEndpoints.splice(i, 1);
						break;
					}
				}

			}
		}
	}


}