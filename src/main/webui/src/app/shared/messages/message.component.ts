/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';

export const MESSAGE_TYPE_NAMES = ['ERROR', 'WARN', 'INFO', 'SUCCESS'] as const;

export type MessageType = typeof MESSAGE_TYPE_NAMES[number];


@Component({
	standalone: true,
	selector: 'app-message',
	imports: [
		NgClass
	],
	templateUrl: './message.component.html',
	styleUrl: './message.component.css'
})
export class MessageComponent {


	/**
	 * The type of message.
	 */
	@Input()
	public type: MessageType = 'INFO';

	/**
	 *  Create the component.
	 */
	constructor(
	) {

	}


}
