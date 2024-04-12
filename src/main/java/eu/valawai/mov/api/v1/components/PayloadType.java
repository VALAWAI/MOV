/*
  Copyright 2022-2026 VALAWAI

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
	ARRAY,

	/**
	 * The constant payload schema.
	 *
	 * @see ConstantPayloadSchema
	 */
	CONST,

	/**
	 * The reference payload schema.
	 *
	 * @see ReferencePayloadSchema
	 */
	REF,

	/**
	 * The payload schema to select one schema from a list.
	 *
	 * @see OneOfPayloadSchema
	 */
	ONE_OF,

	/**
	 * The reference payload to select any schema from a list.
	 *
	 * @see AnyOfPayloadSchema
	 */
	ANY_OF,

	/**
	 * The reference payload schema formed by a list of schemas.
	 *
	 * @see AllOfPayloadSchema
	 */
	ALL_OF;
}
