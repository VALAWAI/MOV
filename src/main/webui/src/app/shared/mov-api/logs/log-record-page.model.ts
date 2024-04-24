/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { LogRecord } from "./log-record.model";

/**
 * A page with some logs.
 *
 * @author VALAWAI
 */
export class LogRecordPage {

	/**
	 * The number of logs that satisfy the query.
	 */
	public total: number = 0;

	/**
	 * The offset of the first returned log.
	 */
	public offset: number = 0;

	/**
	 * The logs that match the query.
	 */
	public logs: LogRecord[] | null = null;

}
