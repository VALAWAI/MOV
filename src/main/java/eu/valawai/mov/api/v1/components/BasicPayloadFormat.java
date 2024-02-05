/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

/**
 * The possible formats for the {@link BasicPayloadSchema}.
 *
 * @see BasicPayloadSchema
 *
 * @author VALAWAI
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
