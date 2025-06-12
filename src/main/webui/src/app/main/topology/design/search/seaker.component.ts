/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component,  OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * Thei component allow to edit the seakerurtion of the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-seaker',
	imports: [
	],
	templateUrl: './seaker.component.html'
})
export class TopologySeakerComponent implements OnInit {

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

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_seaker_code_page-title:Defined topologies`);

	}

}
