/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, ChangeDetectionStrategy } from '@angular/core';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

@Component({
	standalone: true,
	selector: 'app-loading',
	imports: [MatProgressSpinner],
	templateUrl: './loading.component.html',
	changeDetection: ChangeDetectionStrategy.Eager,
	styleUrls: ['./loading.component.css']
})
export class LoadingComponent {

	constructor() { }

}
