/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

/**
 * The type of {@link PayloadSchema}.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
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
