/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";
import { PayloadSchema } from "./payload-schema.model";

/**
 *  A payload that is represented by an array of values.
 *
 * @author VALAWAI
 */
export class ArrayPayloadSchema extends AbstractPayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "ARRAY";

	/**
	 * The type for the elements on the array.
	 */
	public items: PayloadSchema | null = null;

}
