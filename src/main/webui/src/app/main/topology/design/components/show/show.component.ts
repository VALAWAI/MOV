/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * This is used to show the detail of a component in the library.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-show-library',
	imports: [
	],
	templateUrl: './show.component.html'
})
export class ShowLibraryComponent implements OnInit {

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

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_show_code_page-title:Show components library`);

	}

}
