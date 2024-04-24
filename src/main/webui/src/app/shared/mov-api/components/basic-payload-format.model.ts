/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * The names of the basic payload format names.
 *
 * @author VALAWAI
 */
export const BASIC_PAYLOAD_FORMAT_NAMES = ['INTEGER', 'NUMBER', 'BOOLEAN', 'STRING'] as const;

/**
 * The possible formats for the {@link BasicPayloadSchema}.
 *
 * @author VALAWAI
 */
export type BasicPayloadFormat = typeof BASIC_PAYLOAD_FORMAT_NAMES[number];