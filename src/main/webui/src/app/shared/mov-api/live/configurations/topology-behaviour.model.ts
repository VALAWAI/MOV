/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * The names of possible behaviors to do when process an event.
 *
 * @author VALAWAI
 */
export const TOPOLOGY_BEHAVIOR_NAMES = [
	'DO_NOTHING'
	, 'AUTO_DISCOVER'
	, 'APPLY_TOPOLOGY'
	, 'APPLY_TOPOLOGY_OR_AUTO_DISCOVER'
] as const;

/**
 * Enumerates the possible behaviors to do when process an event.
 *
 * @author VALAWAI
 */
export type TopologyBehavior = typeof TOPOLOGY_BEHAVIOR_NAMES[number];
