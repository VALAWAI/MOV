/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';

/**
 * This is used to search for a component defined in the library of components.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-search-library',
	imports: [
	],
	templateUrl: './search.component.html'
})
export class SearchLibraryComponent implements OnInit {

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

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_search_code_page-title:Components library`);

	}

}
