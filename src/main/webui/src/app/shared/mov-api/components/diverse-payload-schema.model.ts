/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { AbstractPayloadSchema } from "./abstract-payload-schema.model";
import { PayloadSchema } from "./payload-schema.model";

/**
 *  A payload schema that is defined a a set of different types.
 *
 * @author VALAWAI
 */
export abstract class DiversePayloadSchema extends AbstractPayloadSchema {

	/**
	 * The possible types that can be used on this schema.
	 */
	public items: PayloadSchema[] | null = null;

}
