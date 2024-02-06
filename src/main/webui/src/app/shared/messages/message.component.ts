/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { Component, Input } from '@angular/core';

@Component({
	selector: 'app-message',
	templateUrl: './message.component.html',
	styleUrls: ['./message.component.css']
})
export class MessageComponent {

	/**
	 * The status of the user.
	 */
	@Input()
	public type: 'error' | 'warn' | 'info' | 'success' | 'none' = 'info';


	/**
	 * Create the component.
	 */
	constructor(
	) { }

}
