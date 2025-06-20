/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v2.design.components.ComponentDefinitionTest;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntities;
import eu.valawai.mov.persistence.design.topology.TopologyGraphNode;

/**
 * Test the {@link TopologyNode}.
 *
 * @see TopologyNode
 *
 * @author VALAWAI
 */
public class TopologyNodeTest extends ModelTestCase<TopologyNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyNode createEmptyModel() {

		return new TopologyNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyNode model) {

		model.tag = ValueGenerator.nextPattern("node_{0}");
		model.position = new PointTest().nextModel();
		model.component = new ComponentDefinitionTest().nextModel();
	}

	/**
	 * Create the {@link TopologyNode} with the data define in the
	 * {@link TopologyGraphNode}.
	 *
	 * @param entity to get the data.
	 *
	 * @return the model with the data of the entity.
	 */
	public static TopologyNode from(TopologyGraphNode entity) {

		if (entity == null) {
			return null;

		} else {

			final var model = new TopologyNode();
			model.tag = entity.tag;
			model.position = new Point();
			model.position.x = entity.x;
			model.position.y = entity.y;
			final var componentEntity = ComponentDefinitionEntities.getById(entity.componentRef);
			model.component = ComponentDefinitionTest.from(componentEntity);
			return model;

		}
	}

}
