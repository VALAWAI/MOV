/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { MovApiService, Info, HealthInfo } from 'src/app/shared/mov-api';
import { HealthStatusComponent } from './health-status.component';
import { NgFor } from '@angular/common';


@Component({
	standalone: true,
    selector: 'app-status',
    imports: [
        HealthStatusComponent,
        NgFor
	    ],
    templateUrl: './status.component.html',
    styleUrl: './status.component.css'
})
export class StatusComponent implements OnInit, OnDestroy {

	/**
	 * The informaiton of the started MOV.
	 */
	public info: Info | null = null;

	/**
	 * The informaiton of the started MOV.
	 */
	public health: HealthInfo | null = null;

	/**
	 * The identifier of the timer.
	 */
	private timeoutID: ReturnType<typeof setTimeout> | null = null;

	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the status @@main_status_code_page-title:Status`);
		this.mov.getHelp().subscribe(
			{
				next: info => this.info = info
			}

		);
		this.updateHealth();

	}

	/**
	 * Called when has to update the health information.
	 */
	public updateHealth() {


		this.mov.getHealth().subscribe(
			{
				next: health => {

					this.health = health;
					this.timeoutID = setTimeout(() => this.updateHealth(), 30000);

				}
			}
		)
	}

	/**
	 * Finalizes the component.
	 */
	ngOnDestroy(): void {

		if (this.timeoutID != null) {

			clearTimeout(this.timeoutID);
		}
	}

}
