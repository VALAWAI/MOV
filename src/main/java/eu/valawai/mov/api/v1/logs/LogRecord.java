/*
  Copyright 2023 UDT-IA, IIIA-CSIC

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
@Schema(title = "A log message that happened on VALAWAI.")
public class LogRecord extends Model {

	/**
	 * The level of the log.
	 */
	@Schema(title = "The level of the log.")
	public LogLevel level;

	/**
	 * The message of the log.
	 */
	@Schema(title = "The log message.")
	public String message;

	/**
	 * The payload of the log.
	 */
	@Schema(title = "The payload associated to the log.")
	public String payload;

	/**
	 * The timestamp when the log has added.
	 */
	@Schema(title = "The timestamp when the .")
	public long timestamp;

}
