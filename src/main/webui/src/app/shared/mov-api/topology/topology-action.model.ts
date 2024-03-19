/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * The names of the topology action names.
 *
 * @author VALAWAI
 */
export const TOPOLOGY_ACTION_NAMES = ['ENABLE', 'DISABLE', 'REMOVE'] as const;

/**
 * The possible actions to do over the topology.
 *
 * @author VALAWAI
 */
export type TopologyAction = typeof TOPOLOGY_ACTION_NAMES[number];