/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component,  OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-status',
	imports: [
	],
	templateUrl: './status.component.html'
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

		this.header.changeHeaderTitle($localize`:The header title for the topology libe status@@main_topology_live_status_code_page-title:Live topology`);

	}

}
