/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LogRecord } from 'src/app/shared/mov-api';

@Component({
	selector: 'app-show-log-dialog',
	templateUrl: './show-log.dialog.html',
	styleUrls: ['./show-log.dialog.css']
})
export class ShowLogDialog {

	/**
	 * The payload tof the log.
	 */
	public payload: any | null = null;

	/**
	 * Create teh compoennt.
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