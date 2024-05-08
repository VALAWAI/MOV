/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A definition of a schema that describe an object.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = "A description of an object payload.")
public class ObjectPayloadSchema extends PayloadSchema {

	/**
	 * The identifier of the object when it is a referenced.
	 */
	@Schema(description = "The identifier of the object if it is references.")
	public Integer id;

	/**
	 * The properties that define the object.
	 */
	@Schema(description = "The properties that define the object attributes.")
	public TreeMap<String, PayloadSchema> properties;

	/**
	 * Create a new object payload schema.
	 */
	public ObjectPayloadSchema() {

		this.type = PayloadType.OBJECT;
		this.properties = new TreeMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean matchPayload(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (other instanceof final ObjectPayloadSchema object) {

			for (final var key : this.properties.keySet()) {

				if (!object.properties.containsKey(key)) {

					return false;

				} else {

					final var source = this.properties.get(key);
					final var target = object.properties.get(key);
					if (!source.match(target, references)) {

						return false;
					}
				}
			}

			return true;
		}
		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (this.id != null && !references.containsKey(this.id)) {

			references.put(this.id, this);

		}
		return super.match(other, references);
	}

}
