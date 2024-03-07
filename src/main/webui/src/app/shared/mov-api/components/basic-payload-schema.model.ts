/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";
import { BasicPayloadFormat } from "./basic-payload-format.model";

/**
 * The basic payload schema.
 *
 * @author VALAWAI
 */
export class BasicPayloadSchema extends AbstractPayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "BASIC";

	/**
	 * The format of the basic type.
	 */
	public format: BasicPayloadFormat | null = null;

}
