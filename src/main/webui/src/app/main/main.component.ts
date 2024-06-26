/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { MainService } from './main.service';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent {

	/**
	 * The title for the header.
	 */
	public headerTitle: Observable<string>;


	/**
	 *  Create the component.
	 */
	constructor(
		private main: MainService
	) {

		this.headerTitle = this.main.headerTitle();
	}


}
