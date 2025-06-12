/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * This is used to manage the posible components taht cna be used into the design of a topology.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-library',
	imports: [
	],
	templateUrl: './components.component.html'
})
export class ComponentsLibraryComponent implements OnInit {

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

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_code_page-title:Components library`);

	}

}
