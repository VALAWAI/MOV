/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.TreeMap;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A definition of a schema that describe an object.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(title = "A description of an object payload.")
public class ObjectPayloadSchema extends PayloadSchema {

	/**
	 * The properties that define the object.
	 */
	@Schema(title = "The properties that define the object attributes.")
	public TreeMap<String, PayloadSchema> properties;

	/**
	 * Create a new object payload schema.
	 */
	public ObjectPayloadSchema() {

		this.type = PayloadType.OBJECT;
		this.properties = new TreeMap<>();
	}

}
