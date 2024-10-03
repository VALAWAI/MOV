/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, Input } from '@angular/core';
import { HealthStatus } from 'src/app/shared/mov-api';


@Component({
	selector: 'app-health-status',
	templateUrl: './health-status.component.html',
	styleUrls: ['./health-status.component.css']
})
export class HealthStatusComponent {


	/**
	 * This is {@code true} if the service is running.
	 */
	public isUp: boolean = false;


	/**
	 *  Create the component.
	 */
	constructor() {

	}

	/**
	 * Updtae the status of the component.
	 */
	@Input()
	public set status(status: HealthStatus | undefined) {

		this.isUp = status == 'UP';
	}

}
