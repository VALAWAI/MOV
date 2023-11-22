/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import java.util.TreeSet;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A payload that is defined of one value of a set.
 *
 * @see PayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "A payload that can be one of the values defined on a set,")
public class EnumPayloadSchema extends PayloadSchema {

	/**
	 * The possible enum values.
	 */
	@Schema(title = "The possible values.")
	public TreeSet<String> values;

	/**
	 * Create a new enum payload schema.
	 */
	public EnumPayloadSchema() {

		this.type = PayloadType.ENUM;
		this.values = new TreeSet<>();
	}

}
