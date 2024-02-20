/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";
import { PayloadSchema } from "./payload-schema.model";

/**
 *  A definition of a schema that describe an object.
 *
 * @author UDT-IA, IIIA-CSIC
 */
export class ObjectPayloadSchema extends AbstractPayloadSchema {

	/**
	 * The type of the payload.
	 */
	public override readonly type = "OBJECT";

	/**
	 * The properties that define the object.
	 */
	public properties: Map<string, PayloadSchema> | null = null;

}
