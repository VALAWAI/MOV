/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Pipe, PipeTransform } from '@angular/core';
import { toChannelName } from '@app/shared';

@Pipe({
	name: 'toChannelName',
	standalone: true,
})
export class ToChannelNamePipe implements PipeTransform {


	/**
	 * Return the type from any value.
	 */
	public transform(channel: string | null | undefined): string {

		var name = toChannelName(channel);
		if (name == null) {

			name = '';
		}

		return name;

	}
}