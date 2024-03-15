/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import java.time.Duration;

import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v1.components.ChannelSchemaTest;
import eu.valawai.mov.api.v1.components.MinComponentTest;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.topology.TopologyNode;

/**
 * Test the {@link TopologyConnectionNode}.
 *
 * @see TopologyConnectionNode
 *
 * @author VALAWAI
 */
public class TopologyConnectionNodeTest extends ModelTestCase<TopologyConnectionNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnectionNode createEmptyModel() {

		return new TopologyConnectionNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnectionNode model) {

		model.component = new MinComponentTest().nextModel();
		model.channel = new ChannelSchemaTest().nextModel();

	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static TopologyConnectionNode from(TopologyNode entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new TopologyConnectionNode();
			final ComponentEntity component = (ComponentEntity) ComponentEntity.findById(entity.componentId).await()
					.atMost(Duration.ofSeconds(30));
			model.component = MinComponentTest.from(component);
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.name.equals(entity.channelName)) {

						model.channel = channel;
						break;
					}
				}
			}
			return model;
		}
	}
}
