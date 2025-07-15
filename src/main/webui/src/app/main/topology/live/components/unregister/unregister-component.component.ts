/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';
import { AbstractComponentComponent } from '../abstract-component.component';
import { AsyncPipe, NgIf } from '@angular/common';
import { LoadingComponent } from '@app/shared/loading';
import { MatButton } from '@angular/material/button';

@Component({
	standalone: true,
    selector: 'app-components-unregister',
    imports: [
        NgIf,
        LoadingComponent,
        AsyncPipe,
		MatButton
    ],
    templateUrl: './unregister-component.component.html',
    styleUrl: './unregister-component.component.css'
})
export class UnregisterComponentComponent extends AbstractComponentComponent {

	/**
	 * This is {@code true} if it is unregistering.
	 */
	public unregistering: boolean = false;

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
		this.header.changeHeaderTitle($localize`:The header title for the unregister component@@main_components_unregister_code_page-title:Unregister Component`);

	}

	/**
	 * Called to unregister.
	 */
	public unregister(): void {

		if (this.componentId != null) {

			this.unregistering = true;
			this.mov.unregisterComponent(this.componentId).subscribe(
				{
					next: () => {

						this.checkUnregistered();

					},
					error: err => {

						this.unregistering = false;
						this.messages.showError($localize`:The error message when cannot unregister a component@@main_components_unregister_code_error-msg:Cannot unregister the component`);
						console.error(err);
					}
				}
			);

		}
	}

	/**
	 * Check if the component is unregistered.
	 */
	private checkUnregistered(iter: number = 60) {

		if (this.componentId != null) {

			this.mov.getComponent(this.componentId).subscribe(
				{
					next: () => {

						if (iter == 0) {

							this.unregistering = false;
							this.messages.showError($localize`:The error message when cannot unregister a component@@main_components_unregister_code_error-msg:Cannot unregister the component`);

						} else {

							setTimeout(() => this.checkUnregistered(iter - 1), 1000);
						}

					},
					error: () => {

						this.messages.showSuccess($localize`:The success message when unregistered a component@@main_components_unregister_code_success-msg:Unregistered the component`);
						this.router.navigate(["/main/topology/live/components"]);

					}
				}
			);
		}

	}


}
