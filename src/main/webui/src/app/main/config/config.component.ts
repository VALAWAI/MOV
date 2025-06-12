/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component,  OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * Thei component allow to edit the configurtion of the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-config',
	imports: [
	],
	templateUrl: './config.component.html'
})
export class ConfigComponent implements OnInit {

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

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_config_code_page-title:Settings`);

	}

}
