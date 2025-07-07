/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
import { Pipe, PipeTransform } from '@angular/core';
import { EndpointData } from './editor.models';

@Pipe({
	standalone:true,
	name: 'channelToName'
})
export class ChannleToNamePipe implements PipeTransform {

	/**
	 * Return the name for a channel.
	 */
	public transform(value: string | null | undefined): string {

		if (value != null) {

			return EndpointData.channelToEndpointName(value);
		}
		return '';

	}

}