/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable, OnInit } from '@angular/core';

import { Observable, EMPTY } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { MessagesService } from '@shared/messages';
import { MovApiService, Component } from '@shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable()
export abstract class AbstractComponentComponent implements OnInit {

	/**
	 * The indeitifer of the component to show.
	 */
	public componentId: string | null = null;

	/**
	 * The component to show.
	 */
	public component$: Observable<Component> | null = null;


	/**
	 *  Create the component.
	 */
	constructor(
		protected mov: MovApiService,
		protected route: ActivatedRoute,
		protected router: Router,
		protected messages: MessagesService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.component$ = this.route.paramMap.pipe(
			switchMap(
				(params) => {

					this.componentId = params.get('id');
					if (this.componentId != null) {

						return this.mov.getComponent(this.componentId).pipe(
							catchError(err => {

								this.messages.showError($localize`:The error message when can get the component@@main_components_abstract_code_get-error:Cannot get the component information`);
								console.error(err);
								this.router.navigate(["/main/topology/live/components"]);
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
