/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { MovApiService, Info } from 'src/app/shared/mov-api';


@Component({
	selector: 'app-status',
	templateUrl: './status.component.html',
	styleUrls: ['./status.component.css']
})
export class StatusComponent implements OnInit {

	/**
	 * The informaiton of the started MOV.
	 */
	public info: Info | null = null;


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the status @@main_status_code_page-title:Status`);
		this.mov.getHelp().subscribe(
			{
				next: info => this.info = info
			}

		);

	}

}
