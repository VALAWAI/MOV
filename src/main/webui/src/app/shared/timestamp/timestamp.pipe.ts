/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';

/*
 * Convert a timestamp to a date.
 * See:
 *   DatePipe
*/
@Pipe({
	standalone: false,
	name: 'timestamp'
})
export class TimestampPipe implements PipeTransform {

	/**
	 * Crete the pipe.
	 */
	constructor(private date: DatePipe) { }

	/**
	 * Convert the time stmap to a string.
	 */
	transform(value: number | null | undefined, format?: string, timezone?: string, locale?: string): string | null {

		if (value != null && value > -1) {

			value = value * 1000;


		} else {

			value = 0;

		}

		return this.date.transform(value, format, timezone, locale);

	}
}