/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

/**
 * The level of a {@link LogRecord}.
 *
 * @see LogRecord
 *
 * @author VALAWAI
 */
public enum LogLevel {

	/**
	 * A log message of the level error.
	 */
	ERROR,

	/**
	 * A log message of the level warning.
	 */
	WARN,

	/**
	 * A log message of the level info.
	 */
	INFO,

	/**
	 * A log message of the level debug.
	 */
	DEBUG;

}
