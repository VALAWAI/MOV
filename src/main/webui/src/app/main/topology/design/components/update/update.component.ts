/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ConfigService } from '@app/shared';
import { LoadingComponent } from '@app/shared/loading';
import { MessagesService } from '@app/shared/messages';
import { MovApiService } from '@app/shared/mov-api';
import { MainService } from 'src/app/main';

/**
 * This is used to manage the posible update taht cna be used into the design of a topology.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-update-library',
	imports: [
		CommonModule,
		LoadingComponent
	],
	templateUrl: './update.component.html'
})
export class UpdateLibraryComponent implements OnInit {

	/**
	 *  The service over the main view. 
	 */
	private readonly header = inject(MainService);

	/**
	 * The service to access the APP configuration.
	 */
	private readonly conf = inject(ConfigService);

	/**
	 * Service to access to the MOV API.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The service to show messages.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * The router of the APP.
	 */
	private readonly router = inject(Router);

	/**
	 * This si {@code true} if it is updating.
	 */
	public updating: boolean = false;

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_update_code_page-title:Update components library`);

	}

	/**
	 * Called whne is updating the library.
	 */
	public update() {

		this.api.refreshComponentsLibrary().subscribe(
			{
				next: () => {

					this.updating = true;
					this.checkIfLibraryHasBeenRefreshed();
				},
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}

	/**
	 * Check if the library has been refreshed.
	 */
	private checkIfLibraryHasBeenRefreshed(zeros: number = 0, now: number = Math.floor(Date.now() / 1000)) {

		this.api.getComponentsLibraryStatus().subscribe(
			{
				next: status => {

					var newZeros = 0;
					if (status.componentCount == 0) {

						newZeros = zeros + 1;

					} else if (status.newestComponentTimestamp >= now && status.oldestComponentTimestamp / status.newestComponentTimestamp > 0.98) {
						//finished to update
						this.router.navigate(["/main/topology/design/components/search"]);
					}

					if (newZeros > this.conf.pollingIterations) {

						this.updating = false;
						this.messages.showError($localize`:The error message when can not update the library@@main_topology_design_components_update_code_update-error-msg:Cannot update the components library`);

					} else {

						setTimeout(() => this.checkIfLibraryHasBeenRefreshed(newZeros, now), this.conf.pollingTime);
					}
				},
				error: err => this.messages.showMOVConnectionError(err)
			}

		);

	}
}
