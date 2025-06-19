/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, model } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from "@angular/material/dialog";


@Component({
	standalone: true,
	selector: 'dialog-confirm-save-before-change',
	templateUrl: 'confirm-save-before-change.dialog.html',
	imports: [
		MatButtonModule,
		MatDialogModule
	],
})
export class ConfirmSaveBeforeChangeDialog {


}