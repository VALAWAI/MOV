/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { Observable, EMPTY } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService, Component as VComponent } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
	selector: 'app-showcomponent',
	templateUrl: './show-component.component.html',
	styleUrls: ['./show-component.component.css']
})
export class ShowComponentComponent implements OnInit {

	/**
	 * The indeitifer of teh component to show.
	 */
	public componentId: string | null = null;

	/**
	 * The component to show.
	 */
	public component$: Observable<VComponent> | null = null;


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private route: ActivatedRoute,
		private router: Router,
		private messages: MessagesService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the show component@@main_components_show_code_page-title:Show Component`);
		this.component$ = this.route.paramMap.pipe(
			switchMap(
				(params) => {

					this.componentId = params.get('id');
					if (this.componentId != null) {

						return this.mov.getComponent(this.componentId).pipe(
							catchError(err => {

								this.messages.showError($localize`:The error message when can get the component@@main_components_show_code_get-error:Cannot get the component to show`);
								console.error(err);
								this.router.navigate(["/main/components"]);
								return EMPTY;
							})
						);

					} else {

						return EMPTY;
					}
				}
			)
		);
	}


}
