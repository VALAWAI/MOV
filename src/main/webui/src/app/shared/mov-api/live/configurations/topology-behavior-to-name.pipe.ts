/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Pipe, PipeTransform } from '@angular/core';
import { TopologyBehavior } from './topology-behavior.model';

/*
 * Convert a TopologyBehavior to a name.
 * See:
 *   DatePipe
*/
@Pipe({
	standalone: true,
	name: 'topologyBehaviorToName'
})
export class TopologyBehaviorToNamePipe implements PipeTransform {


	/**
	 * Convert the time stmap to a string.
	 */
	transform(value: TopologyBehavior | string | null | undefined): string {

		if (value != null) {

			var upper = value.toUpperCase();
			if (upper === 'DO_NOTHING') {

				return $localize`:The name for the DO_NOTHING topology behavior@@topology-behavior_do-nothing-name:Do nothing`;

			} else if (upper === 'APPLY_TOPOLOGY') {

				return $localize`:The name for the APPLY_TOPOLOGY topology behavior@@topology-behavior_apply-topology-name:Apply topology`;

			} else if (upper === 'APPLY_TOPOLOGY_OR_AUTO_DISCOVER') {

				return $localize`:The name for the APPLY_TOPOLOGY_OR_AUTO_DISCOVER topology behavior@@topology-behavior_apply-topology-or-auto-discover-name:Apply topology or auto discover`;
			}
		}
		return $localize`:The name for the AUTO_DISCOVER topology behavior@@topology-behavior_auto-discover-name:Auto discover`;

	}
}