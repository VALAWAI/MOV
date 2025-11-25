/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ComponentType } from '@shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-content-type-badge',
	imports: [
		CommonModule
	],
	templateUrl: './component-type-badge.component.html'
})
export class ComponentTypeBadgeComponent {

	/**
	 * The stored type.
	 */
	private _type: ComponentType = 'C0';

	/**
	 * The type to display.
	 */
	public get type(): ComponentType {

		return this._type;
	}

	/**
	 * The type of the component.
	 */
	@Input()
	public set type(type: ComponentType | null | undefined) {

		if (type != null) {

			this._type = type;

		} else {

			this._type = 'C0';
		}


	}

}
