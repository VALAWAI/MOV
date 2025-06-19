/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import jakarta.validation.constraints.NotNull;

/**
 * Generic class to test the classes that extends the {@link MinTopology}.
 *
 * @param <T> The type of the class to test.
 *
 * @author VALAWAI
 */
public abstract class MinTopologyTestCase<T extends MinTopology> extends ModelTestCase<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(T model) {

		model.id = ValueGenerator.nextObjectId();
		model.name = ValueGenerator.nextPattern("Component name {0}");
		model.description = ValueGenerator.nextPattern("Component description {0}");
	}

	/**
	 * Add to the specified model the data of the entity.
	 *
	 * @param model  to fill in.
	 * @param entity to get the data to fill in the model.
	 */
	public static <M extends MinTopology> void fillInWith(@NotNull M model, @NotNull TopologyGraphEntity entity) {

		model.id = entity.id;
		model.name = entity.name;
		model.description = entity.description;

	}

}
