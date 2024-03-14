/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import eu.valawai.mov.persistence.components.ComponentEntity;

/**
 * Test the {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @author VALAWAI
 */
public class MinComponentTest extends AbstractMinComponentTestCase<MinComponent> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinComponent createEmptyModel() {

		return new MinComponent();
	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static MinComponent from(ComponentEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new MinComponent();
			model.id = entity.id;
			model.type = entity.type;
			model.name = entity.name;
			model.description = entity.description;
			return model;

		}
	}
}
