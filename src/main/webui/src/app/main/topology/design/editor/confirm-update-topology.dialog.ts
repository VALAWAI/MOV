/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { GraphModule } from "@app/shared/graph";


@Component({
	standalone: true,
	selector: 'dialog-confirm-update-topology',
	templateUrl: './confirm-update-topology.dialog.html',
	imports: [
		MatButtonModule,
		MatDialogModule,
		GraphModule
	],
})
export class ConfirmUpdateTopologyDialog {

	/**
	 * The update event over the topology.
	 */
	public data = inject(MAT_DIALOG_DATA);


}