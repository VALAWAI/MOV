/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

/**
 * The possible formats for the {@link BasicPayloadSchema}.
 *
 * @see BasicPayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum BasicPayloadFormat {

	/**
	 * The type is an integer.
	 */
	INTEGER,

	/**
	 * The type is a number.
	 */
	NUMBER,

	/**
	 * The type is a boolean.
	 */
	BOOLEAN,

	/**
	 * The type is a string.
	 */
	STRING;

}
