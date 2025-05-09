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

	/**
	 * Check if this page is equals to another.
	 */
	public static equals(source: LogRecordPage | null | undefined, target: LogRecordPage | null | undefined): boolean {

		if (source == null && target == null) {

			return true;

		} else if (
			source != null
			&& target != null
			&& source.total === target.total
		) {
			if ((source.logs == null || source.logs.length == 0)
				&& (target.logs == null || target.logs.length == 0)
			) {

				return true;

			} else if (
				source.logs != null
				&& target.logs != null
				&& source.logs.length === target.logs.length
			) {

				for (var i = 0; i < source.logs.length; i++) {

					var sourceComponent = source.logs[i];
					var targetComponent = target.logs[i];
					if (!LogRecord.equals(sourceComponent, targetComponent)) {

						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

}
