/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.components.ComponentEntity;

/**
 * Test the {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @author UDT-IA, IIIA-CSIC
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
	 * {@inheritDoc}
	 */
	@Override
	protected void fillIn(MinComponent model) {

		model.id = ValueGenerator.nextObjectId();
		model.name = ValueGenerator.nextPattern("Name of component  {0}");
		model.description = ValueGenerator.nextPattern("Description of component  {0}");
		model.type = ValueGenerator.next(ComponentType.values());

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
