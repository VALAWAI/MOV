/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';
import { AbstractTopologyConnectionComponent } from '../abstract-topology-connection.component';



@Component({
	selector: 'app-show-topology-connection',
	templateUrl: './show-topology-connection.component.html',
	styleUrls: ['./show-topology-connection.component.css']
})
export class ShowTopologyConnectionComponent extends AbstractTopologyConnectionComponent {

	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		protected override mov: MovApiService,
		protected override route: ActivatedRoute,
		protected override router: Router,
		protected override messages: MessagesService
	) {

		super(mov, route, router, messages);
	}

	/**
	 * Initialize the component.
	 */
	override ngOnInit(): void {

		super.ngOnInit();
		this.header.changeHeaderTitle($localize`:The header title for the show topology connection@@main_topology_connections_show_code_page-title:Show topology conection`);
	}


}
