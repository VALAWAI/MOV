/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

/**
 * The type of {@link PayloadSchema}.
 *
 * @see PayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum PayloadType {

	/**
	 * The basic payload schema.
	 *
	 * @see BasicPayloadSchema
	 */
	BASIC,

	/**
	 * The enum payload schema.
	 *
	 * @see EnumPayloadSchema
	 */
	ENUM,

	/**
	 * The object payload schema.
	 *
	 * @see ObjectPayloadSchema
	 */
	OBJECT,

	/**
	 * The array payload schema.
	 *
	 * @see ArrayPayloadSchema
	 */
	ARRAY;
}
