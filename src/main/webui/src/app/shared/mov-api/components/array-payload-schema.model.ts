/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { DiversePayloadSchema } from "./diverse-payload-schema.model";

/**
 *  A payload that is represented by an array of values.
 *
 * @author VALAWAI
 */
export class ArrayPayloadSchema extends DiversePayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "ARRAY";

}
