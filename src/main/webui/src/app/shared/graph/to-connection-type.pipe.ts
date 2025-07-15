/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Pipe, PipeTransform } from '@angular/core';
import { EFConnectionType } from '@foblex/flow';

@Pipe({
	name: 'toConnectionType',
	standalone: true,
})
export class ToConnectionTypePipe implements PipeTransform {


	/**
	 * Return the type from any value.
	 */
	public transform(type: EFConnectionType | string | null | undefined): EFConnectionType {

		var fType = EFConnectionType.BEZIER;

		if (type) {

			var lower = type.toLowerCase().trim();
			if (lower == EFConnectionType.STRAIGHT) {

				fType = EFConnectionType.STRAIGHT;

			} else if (lower == EFConnectionType.SEGMENT) {

				fType = EFConnectionType.SEGMENT;
			}

		}

		return fType;
	}
}