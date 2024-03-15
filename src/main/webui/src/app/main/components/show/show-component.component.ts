/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';
import { AbstractComponentComponent } from '../abstract-component.component';
import { Subscription } from 'rxjs';


@Component({
	selector: 'app-show-component',
	templateUrl: './show-component.component.html',
	styleUrls: ['./show-component.component.css']
})
export class ShowComponentComponent extends AbstractComponentComponent implements OnDestroy {

	/**
	 * The connection that show its connection.
	 */
	public connectionId: string | null = null;

	/**
	 * Subscription for the connection identifier changes.
	 */
	private connectionIdChanged: Subscription | null = null;

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
		this.header.changeHeaderTitle($localize`:The header title for the show component@@main_components_show_code_page-title:Show Component`);

		this.connectionIdChanged = this.route.queryParamMap.subscribe({
			next: (params) => {

				this.connectionId = params.get("connectionId");
			}
		});
	}

	/**
	 * Unsubscribe the connection.
	 */
	ngOnDestroy(): void {

		if (this.connectionIdChanged != null) {

			this.connectionIdChanged.unsubscribe();
			this.connectionIdChanged = null;
		}
	}


}
