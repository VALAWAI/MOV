/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";

/**
 * A payload that is defined as a constant value.
 *
 * @author VALAWAI
 */
export class ConstantPayloadSchema extends AbstractPayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "CONST";

	/**
	 * The constant value.
	 */
	public value: string | null = null;

}
