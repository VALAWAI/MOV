/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatListModule } from "@angular/material/list";
import { GraphModule } from "@app/shared/graph";
import { ChannelSchema } from "@app/shared/mov-api";


@Component({
	standalone: true,
	selector: 'dialog-select-channel',
	templateUrl: './select-channel.dialog.html',
	imports: [
		CommonModule,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
		GraphModule,
		MatListModule,
		ReactiveFormsModule
	],
})
export class SelectChannelDialog {

	/**
	 * The update event over the topology.
	 */
	public data: ChannelSchema[] = inject(MAT_DIALOG_DATA);

	/**
	 * The form control to define the target channle to select.
	 */
	public selectedChannel = new FormControl<ChannelSchema[] | null>(null, Validators.required);


}