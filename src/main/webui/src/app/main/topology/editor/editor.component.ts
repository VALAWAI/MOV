/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MainService } from '@app/main/main.service';

@Component({
	standalone: true,
	selector: 'app-topology-editor',
	imports: [
		CommonModule,
	],
	templateUrl: './editor.component.html',
	styleUrl: './editor.component.css'
})
export class TopologyEditorComponent implements OnInit, OnDestroy {


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
	) {

	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the topology editor@@main_topology_editor_code_page-title:Topology Editor`);

	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {


	}

}
