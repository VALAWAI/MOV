/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, Inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { TimestampPipe } from '@app/shared/timestamp';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { LogRecord } from '@shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-show-log-dialog',
	imports: [
		MatDialogContent,
		MatDialogActions,
		MatButton,
		NgxJsonViewerModule,
		MatDialogClose,
		TimestampPipe
	],
	templateUrl: './show-log.dialog.html',
	styleUrl: './show-log.dialog.css'
})
export class ShowLogDialog {

	/**
	 * The payload tof the log.
	 */
	public payload: any | null = null;

	/**
	 * Create the compoennt.
	 */
	constructor(
		@Inject(MAT_DIALOG_DATA) public data: LogRecord
	) {

		if (data.payload != null) {

			try {

				this.payload = JSON.parse(data.payload);

			} catch (err) {

				console.error(err);
			}
		}

	}

}