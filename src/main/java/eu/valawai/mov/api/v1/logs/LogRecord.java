/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A log message that has been done in VALAWAI.
 *
 * @author VALAWAI
 */
@Schema(description = "A log message that happened on VALAWAI.")
public class LogRecord extends Model {

	/**
	 * The level of the log.
	 */
	@Schema(description = "The level of the log.")
	public LogLevel level;

	/**
	 * The message of the log.
	 */
	@Schema(description = "The log message.")
	public String message;

	/**
	 * The payload of the log.
	 */
	@Schema(description = "The payload associated to the log. It is a JSON encoded as a string")
	public String payload;

	/**
	 * The timestamp when the log has added.
	 */
	@Schema(description = "The epoch time, in seconds, when the log happened.")
	public long timestamp;

}
