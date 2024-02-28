/*
	Copyright 2022 UDT-IA, IIIA-CSIC

	Use of this source code is governed by GNU General Public License version 3
	license that can be found in the LICENSE file or at
	https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
	providedIn: 'root'
})
export class MessagesService {

	/**
	 * Create the service.
	 */
	constructor(
		private snackBar: MatSnackBar
	) { }


	/**
	 * Show error message.
	 */
	public showError(text: string) {

		this.show(text, "error-snackbar");
	}

	/**
	 * Show warn message.
	 */
	public showWarn(text: string) {

		this.show(text, "warn-snackbar");
	}

	/**
	 * Show info message.
	 */
	public showInfo(text: string) {

		this.show(text, "info-snackbar");
	}


	/**
	 * Show success message.
	 */
	public showSuccess(text: string) {

		this.show(text, "success-snackbar");
	}


	/**
	 * Show message of the specified class.
	 */
	private show(text: string, clazz: string) {

		this.snackBar.open(text, undefined, { panelClass: clazz, duration: 3000 });
	}

}