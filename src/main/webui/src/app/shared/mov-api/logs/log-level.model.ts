/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

/**
 * The names of the log levels.
 *
 * @author VALAWAI
 */
export const LOG_LEVEL_NAMES = [
	'ERROR'
	, 'WARN'
	, 'INFO'
	, 'DEBUG'
] as const;

/**
 * The possible level of the log messages.
 *
 * @author VALAWAI
 */
export type LogLevel = typeof LOG_LEVEL_NAMES[number];
