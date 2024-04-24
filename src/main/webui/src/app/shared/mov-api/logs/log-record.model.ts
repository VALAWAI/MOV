/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { LogLevel } from "./log-level.model";
import { MinComponent } from "../components/min-component.model";

/**
 * A log message that has been done in VALAWAI.
 *
 * @author VALAWAI
 */
export class LogRecord {

	/**
	 * The timestamp when the log has added.
	 */
	public timestamp: number | null = null;

	/**
	 * The level of the log.
	 */
	public level: LogLevel | null = null;

	/**
	 * The message of the log.
	 */
	public message: string | null = null;

	/**
	 * The payload of the log.
	 */
	public payload: string | null = null;

	/**
	 * The component that has generated teh log.
	 */
	public component: MinComponent | null = null;

}
