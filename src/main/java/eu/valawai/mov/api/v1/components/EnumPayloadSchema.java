/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.TreeSet;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * A payload that is defined of one value of a set.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@JsonRootName("enum")
@Schema(description = "A payload that can be one of the values defined on a set.")
public class EnumPayloadSchema extends PayloadSchema {

	/**
	 * The possible enum values.
	 */
	@Schema(description = "The possible values.")
	public TreeSet<String> values;

	/**
	 * Create a new enum payload schema.
	 */
	public EnumPayloadSchema() {

		this.type = PayloadType.ENUM;
		this.values = new TreeSet<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other) {

		return other instanceof final EnumPayloadSchema schema
				&& (this.values == schema.values || this.values != null && this.values.equals(schema.values));
	}

}
