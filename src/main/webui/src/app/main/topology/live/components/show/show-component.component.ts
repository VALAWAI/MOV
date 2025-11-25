/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy } from '@angular/core';
import { MainService } from '@app/main';
import { MessagesService } from '@shared/messages';
import { MovApiService } from '@shared/mov-api';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AbstractComponentComponent } from '../abstract-component.component';
import { Subscription } from 'rxjs';
import { MatButton } from '@angular/material/button';
import { LoadingComponent } from '@app/shared/loading';
import { ComponentViewComponent } from '@app/shared/component/view';
import { AsyncPipe, CommonModule } from '@angular/common';


@Component({
	standalone: true,
	selector: 'app-components-show',
	imports: [
		CommonModule,
		MatButton,
		LoadingComponent,
		ComponentViewComponent,
		AsyncPipe,
		RouterLink
	],
	templateUrl: './show-component.component.html',
	styleUrl: './show-component.component.css'
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
