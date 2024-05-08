/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A payload that is represented by an array of values.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A schema for a payload that contains an array of values.")
public class ArrayPayloadSchema extends DiversePayloadSchema {

	/**
	 * Create a new array payload schema.
	 */
	public ArrayPayloadSchema() {

		this.type = PayloadType.ARRAY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean matchPayload(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		return other instanceof ArrayPayloadSchema && super.matchPayload(other, references);
	}

}
