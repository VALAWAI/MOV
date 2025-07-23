/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatListModule, MatSelectionListChange } from '@angular/material/list';
import { MessageComponent } from "@app/shared/messages";
import { MatIconModule } from "@angular/material/icon";
import { CommonModule } from "@angular/common";
import { EditorNode } from "./editor-node.model";
import { EditorEndpoint } from "./editor-endpoint.model";
import { GraphModule } from "@app/shared/graph";
import { ChannelSchema } from "@app/shared/mov-api";


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
		MatIconModule,
		GraphModule
	],
})
export class SelectNodeEndpointsDialog implements OnInit {

	/**
	 * The controller for the selected topology.
	 */
	public data: EditorNode = inject(MAT_DIALOG_DATA);

	/**
	 * The possible channels that can be selected for the node.
	 */
	public channels: ChannelSchema[] = [];

	/**
	 * The endpoints that has been selected.
	 */
	public selectedEndpoints: EditorEndpoint[] = [...this.data.endpoints];

	/**
	 * Calculate the posible endpoints.
	 */
	public ngOnInit() {

		if ('component' in this.data.model && this.data.model.component != null
			&& this.data.model.component.channels != null) {

			this.channels = [... this.data.model.component.channels];
			this.channels.sort((c1, c2) => c1.name!.localeCompare(c2.name!));
		}
	}

	/**
	 * Check if a channel is selected.
	 */
	public isSelected(channel: ChannelSchema): boolean {

		return this.selectedEndpoints.find(e => e.channel === channel.name) != null;

	}


	/**
	 * Called when the selected endpoints has changed. 
	 */
	public endppointsSelectionChange(event: MatSelectionListChange) {

		for (var option of event.options) {

			if (option.selected) {

				var endpoint = new EditorEndpoint(this.data.id, option.value.name, option.value.publish != null);
				this.selectedEndpoints.push(endpoint);

			} else {

				for (var i = 0; i < this.selectedEndpoints.length; i++) {

					if (this.selectedEndpoints[i].channel === option.value.name) {

						this.selectedEndpoints.splice(i, 1);
						break;
					}
				}

			}
		}
	}


}