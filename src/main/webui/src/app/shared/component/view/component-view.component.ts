/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LoadingComponent } from '@app/shared/loading';
import { TimestampPipe } from '@app/shared/timestamp';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { Component as MOVComponent } from 'src/app/shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-component-view',
	imports: [
		NgIf,
		LoadingComponent,
		TimestampPipe,
		NgxJsonViewerModule,
		NgFor
	],
	templateUrl: './component-view.component.html',
	styleUrl: './component-view.component.css'
})
export class ComponentViewComponent {

	/**
	 * The component to view.
	 */
	@Input()
	public component: MOVComponent | null = null;

	/**
	 * Create the component view.
	 */
	constructor() { }




}
