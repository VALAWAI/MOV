/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

/**
 * The names of the component types.
 *
 * @author VALAWAI
 */
export const COMPONENT_TYPE_NAMES = ['C0', 'C1', 'C2'] as const;

/**
 * The possible type of the VALAWAI components.
 *
 * @author VALAWAI
 */
export type ComponentType = typeof COMPONENT_TYPE_NAMES[number];
