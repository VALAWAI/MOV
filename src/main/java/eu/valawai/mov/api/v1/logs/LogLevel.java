/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The level of a {@link LogRecord}.
 *
 * @see LogRecord
 *
 * @author VALAWAI
 */
@Schema(description = "A possible levels of a log.")
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
