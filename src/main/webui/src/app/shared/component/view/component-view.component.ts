/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, Input } from '@angular/core';
import { Component as MOVComponent } from 'src/app/shared/mov-api';

@Component({
	selector: 'app-component-view',
	templateUrl: './component-view.component.html',
	styleUrls: ['./component-view.component.css']
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
