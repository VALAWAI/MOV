/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CombinePayloadSchema } from "./combine-payload-schema.model";
import { PayloadSchema } from "./payload-schema.model";

/**
 *  A payload that is a set of schemas.
 *
 * @author VALAWAI
 */
export class AllOfPayloadSchema extends CombinePayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "ALL_OF";

}
