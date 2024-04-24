/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";

/**
 *  A payload that is defined of one value of a set.
 *
 * @author VALAWAI
 */
export class EnumPayloadSchema extends AbstractPayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "ENUM";

	/**
	 * The possible enum values.
	 */
	public values: string[] | null = null;

}
