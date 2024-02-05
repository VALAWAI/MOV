/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

@Component({
	selector: 'app-status',
	templateUrl: './status.component.html',
	styleUrls: ['./status.component.css']
})
export class StatusComponent implements OnInit {


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the status @@main_status_code_page-title:Status`);

	}

}
