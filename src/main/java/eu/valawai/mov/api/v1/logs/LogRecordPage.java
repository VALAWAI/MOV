/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page with some logs.
 *
 * @see LogRecord
 *
 * @author VALAWAI
 */
@Schema(description = "A page with some logs.")
public class LogRecordPage extends Model {

	/**
	 * The number of logs that satisfy the query.
	 */
	@Schema(description = "The total number of logs that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(description = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The logs that match the query.
	 */
	@Schema(description = "The logs that satisfy the query")
	public List<LogRecord> logs;

}
