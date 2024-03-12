/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.logs;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.Payload;
import jakarta.validation.constraints.NotNull;

/**
 * The information of a log message.
 *
 * @author VALAWAI
 */
@JsonRootName("add_log_payload")
public class AddLogPayload extends Payload {

	/**
	 * The level of the log.
	 */
	@NotNull
	public LogLevel level;

	/**
	 * The message of the log.
	 */
	@NotNull
	public String message;

	/**
	 * The payload of the log.
	 */
	public String payload;

}
