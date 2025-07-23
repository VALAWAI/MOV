/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
	name: 'toCssVar',
	standalone: true,
})
export class ToCssVariablePipe implements PipeTransform {


	/**
	 * Return the css variable of a css value.
	 */
	public transform(css: string | null | undefined): string {


		if (css) {


			return 'var(--' + css + ')';
		}

		return '';
	}
}