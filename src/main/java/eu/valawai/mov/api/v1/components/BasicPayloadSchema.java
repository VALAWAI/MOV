/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The basic payload schema.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(title = "The basic type of the payload.")
public class BasicPayloadSchema extends PayloadSchema {

	/**
	 * The format of the basic type.
	 */
	@Schema(title = "The format of the basic type.")
	public BasicPayloadFormat format;

	/**
	 * Create a new basic payload schema.
	 */
	public BasicPayloadSchema() {

		this.type = PayloadType.BASIC;
	}

	/**
	 * Create a basic payload with specific format.
	 *
	 * @param format of the basic payload.
	 *
	 * @return the basic payload with the specified format.
	 */
	public static BasicPayloadSchema with(BasicPayloadFormat format) {

		final var schema = new BasicPayloadSchema();
		schema.format = format;
		return schema;
	}

}
