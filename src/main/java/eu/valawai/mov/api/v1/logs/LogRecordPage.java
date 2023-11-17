/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.logs;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page with some logs.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class LogRecordPage extends Model {

	/**
	 * The number of logs that satisfy the query.
	 */
	@Schema(title = "The total number of logs that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(title = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The logs that match the query.
	 */
	@Schema(title = "The logs that satisfy the query")
	public List<LogRecord> logs;

}
