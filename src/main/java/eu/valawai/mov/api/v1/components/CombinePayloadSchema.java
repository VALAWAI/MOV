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
 * A schema that is formed by a combination of other schemas.
 *
 * @see PayloadSchema
 *
 * @author VALAWAI
 */
@Schema(description = " A schema that is formed by a combination of other schemas.")
public abstract class CombinePayloadSchema extends DiversePayloadSchema {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean matchPayload(PayloadSchema other, Map<Integer, PayloadSchema> references) {

		if (this.items != null && this.items.size() == 1 && other instanceof final CombinePayloadSchema combine
				&& combine.items != null && combine.items.size() == 1) {

			return this.items.get(0).match(combine.items.get(0), references);

		} else if (other != null && other.getClass() == this.getClass()) {

			return super.matchPayload(other, references);

		} else {

			return false;
		}
	}

}
