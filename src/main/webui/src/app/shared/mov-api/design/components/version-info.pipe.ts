/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { DatePipe } from '@angular/common';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { VersionInfo } from './version-info.model';

/*
 * Convert a VersionInfo to a name.
 * See:
 *   DatePipe
*/
@Pipe({
	standalone: true,
	name: 'versionInfoToName'
})
export class VersionInfoToNamePipe implements PipeTransform {

	/**
	 * The pipe to format a date.
	 */
	private date = inject(DatePipe);

	/**
	 * Convert the time stmap to a string.
	 */
	transform(value: VersionInfo | null | undefined): string {

		var name = '';
		if (value != null) {

			if( value.name != null){
				
				
			}
			if (value.since != null) {

				var since = this.date.transform(value.since * 1000, 'mediumDate');
			}
		}
		return name;

	}
}