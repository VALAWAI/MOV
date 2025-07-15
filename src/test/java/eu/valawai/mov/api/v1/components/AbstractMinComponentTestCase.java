/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntity;

/**
 * Generic test for the classes that extends {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @param <T> type of model to test.
 *
 * @author VALAWAI
 */
public abstract class AbstractMinComponentTestCase<T extends MinComponent> extends ModelTestCase<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(T model) {

		model.id = nextObjectId();
		model.type = next(ComponentType.values());
		final var name = nextPattern("component_{0}");
		model.name = "valawai/" + model.type.name().toLowerCase() + "/" + name;
		model.description = "Description of " + name;

	}

	/**
	 * Fill in the model with the data of an entity.
	 *
	 * @param model  to fill in.
	 * @param entity to get the data.
	 */
	public static <T extends MinComponent> void fillWith(T model, ComponentEntity entity) {

		model.id = entity.id;
		model.type = entity.type;
		model.name = entity.name;
		model.description = entity.description;
	}

}
