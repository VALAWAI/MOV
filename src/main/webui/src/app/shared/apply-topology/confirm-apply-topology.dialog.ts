/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MinTopology } from "@app/shared/mov-api";


@Component({
	standalone: true,
	selector: 'dialog-confirm-apply-topology',
	templateUrl: 'confirm-apply-topology.dialog.html',
	imports: [
		MatButtonModule,
		MatDialogModule
	],
})
export class ConfirmApplyTopologyDialog {

	/**
	 * The topology to apply.
	 */
	public data: MinTopology = inject(MAT_DIALOG_DATA);

}