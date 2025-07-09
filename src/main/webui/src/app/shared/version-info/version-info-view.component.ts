/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule, DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { VersionInfo } from '../mov-api';

@Component({
	standalone: true,
	selector: 'app-version-info-view',
	imports: [CommonModule,DatePipe],
	templateUrl: './version-info-view.component.html',
})
export class VersionInfoViewComponent {


	/**
	 * The version to show.
	 */
	@Input()
	public version: VersionInfo | null = null;

}
