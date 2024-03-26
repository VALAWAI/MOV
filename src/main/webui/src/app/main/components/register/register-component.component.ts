/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component} from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService } from 'src/app/shared/mov-api';


@Component({
	selector: 'app-register-component',
	templateUrl: './register-component.component.html',
	styleUrls: ['./register-component.component.css']
})
export class RegisterComponentComponent {


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private messages:MessagesService
	) {

	}


}
