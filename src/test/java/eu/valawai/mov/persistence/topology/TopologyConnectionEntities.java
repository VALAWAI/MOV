/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import eu.valawai.mov.persistence.components.ComponentEntities;
import io.quarkus.logging.Log;

/**
 * Utility calss to manage the {@link TopologyConnectionEntity} used on tests.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public interface TopologyConnectionEntities {

	/**
	 * Create a new component.
	 *
	 * @return the created component.
	 */
	public static TopologyConnectionEntity nextTopologyConnection() {

		final TopologyConnectionEntity entity = new TopologyConnectionEntity();
		entity.createTimestamp = nextPastTime();
		entity.updateTimestamp = nextPastTime();
		entity.enabled = flipCoin();
		entity.source = new TopologyNode();
		entity.target = new TopologyNode();

		while (entity.source.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						entity.source.componentId = component.id;
						entity.source.channelName = channel.id;
						break;
					}
				}
			}

		}

		while (entity.target.componentId == null) {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.subscribe != null) {

						entity.target.componentId = component.id;
						entity.target.channelName = channel.id;
						break;
					}
				}
			}

		}

		final var stored = entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot persist {}", entity);
			return null;

		}).await().atMost(Duration.ofSeconds(30));
		if (stored == null) {

			fail("Cannot persist a topology connection.");
		}
		return entity;
	}

}
