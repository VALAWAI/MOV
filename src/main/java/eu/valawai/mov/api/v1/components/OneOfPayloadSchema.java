/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A payload that is one of the possible schemas.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A payload that is one of the possible schemas.")
public class OneOfPayloadSchema extends CombinePayloadSchema {

	/**
	 * Create a new array payload schema.
	 */
	public OneOfPayloadSchema() {

		this.type = PayloadType.ONE_OF;
	}

}
