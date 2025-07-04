/*
	Copyright 2022 UDT-IA, IIIA-CSIC

	Use of this source code is governed by GNU General Public License version 3
	license that can be found in the LICENSE file or at
	https://opensource.org/license/gpl-3-0/
*/

import { Component, Inject, Injectable } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBar, MatSnackBarLabel } from '@angular/material/snack-bar';
import { MessageComponent, MessageType } from './message.component';


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

		this.show(text, 'ERROR');
	}

	/**
	 * Show warn message.
	 */
	public showWarn(text: string) {

		this.show(text, 'WARN');
	}

	/**
	 * Show info message.
	 */
	public showInfo(text: string) {

		this.show(text, 'INFO');
	}


	/**
	 * Show success message.
	 */
	public showSuccess(text: string) {

		this.show(text, 'SUCCESS');
	}

	/**
	 * Show error message.
	 */
	public showMOVConnectionError(err: any) {

		var text = $localize`:Error message when can not connect with the MOV@@shared_messages_server-error-msg:Could not connect to the MOV, try again later.`;
		this.show(text, 'ERROR');
		if (err != null) {

			console.error(err);
		}
	}


	/**
	 * Show message of the specified class.
	 */
	private show(message: string, type: MessageType) {

		this.snackBar.openFromComponent(SnackBarMessage, {
			duration: 3000,
			verticalPosition: 'bottom',
			horizontalPosition: 'center',
			data: {
				text: message,
				type: type
			}
		});
	}

}

export class SnackBarMessageData {

	/**
	 * The text to show
	 */
	text: string = '';

	/**
	 * The type of message.
	 */
	type: MessageType = 'INFO';
}


@Component({
	standalone: true,
	selector: 'app-snack-bar-message',
	template: '<app-message matSnackBarLabel [type]="data.type">{{data.text}}</app-message>',
	imports: [MessageComponent, MatSnackBarLabel],
})
export class SnackBarMessage {

	/**
	 *  Create the component.
	 */
	constructor(
		@Inject(MAT_SNACK_BAR_DATA) public data: SnackBarMessageData
	) {

	}

}
