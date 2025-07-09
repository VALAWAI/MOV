/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Directive, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessagesService } from '@app/shared/messages';
import { ComponentDefinition, MovApiService } from '@app/shared/mov-api';
import { Subscription, switchMap, of } from 'rxjs';

/**
 * This is used to show the detail of a component in the library.
 */
@Directive()
export abstract class AbstractComponentDefinitionComponent implements OnInit, OnDestroy {

	/**
	 * The service to show user messages.
	 */
	protected messages = inject(MessagesService);

	/**
	 * The active router.
	 */
	private route = inject(ActivatedRoute);

	/**
	 * The subscription to the route parameters.
	 */
	private pathParamSubscription: Subscription | null = null;

	/**
	 * The service to interact with the MOV.
	 */
	protected api = inject(MovApiService);

	/**
	 * The service to navigate on the app.
	 */
	protected router = inject(Router);


	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.pathParamSubscription = this.route.params.pipe(
			switchMap(
				params => {

					const id = params['id'];
					if (id) {

						return this.api.getComponentDefinition(id);
					}

					return of(null);
				}
			)

		).subscribe(
			{
				next: component => {


					if (component == null) {

						this.router.navigate(['/main/topology/design/components/search']);
						this.messages.showError($localize`:The error message when can not get the component definition@@main_topology_design_component_abstract_code_not-found-error:Component not found`);

					} else {

						this.updateComponent(component);
					}
				},
				error: err => {

					this.messages.showMOVConnectionError(err);
					this.router.navigate(['/main/topology/design/components/search']);
				}
			}

		);

	}

	/**
	 * Liverate the resources of the component.
	 */
	public ngOnDestroy() {

		if (this.pathParamSubscription != null) {

			this.pathParamSubscription.unsubscribe();
			this.pathParamSubscription = null;
		}
	}

	/**
	 * The component to use in the component.
	 */
	protected abstract updateComponent(component: ComponentDefinition): void;


}
