/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;

import java.time.Instant;
import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.live.components.ComponentEntity;

/**
 * Test the {@link Component}.
 *
 * @see Component
 *
 * @author VALAWAI
 */
public class ComponentTest extends AbstractMinComponentTestCase<Component> {

	/**
	 * {@inheritDoc}
	 *
	 * @see Component#Component()
	 */
	@Override
	public Component createEmptyModel() {

		return new Component();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(Component model) {

		super.fillIn(model);
		model.version = nextPattern("{0}.{1}.{2}", 3);
		model.since = rnd().nextLong(0, Instant.now().getEpochSecond());
		model.apiVersion = nextPattern("{0}.{1}.{2}", 3);

		final var max = ValueGenerator.rnd().nextInt(0, 5);
		if (max > 0) {

			model.channels = new ArrayList<>();
			final var builder = new ChannelSchemaTest();
			for (var i = 0; i < max; i++) {

				final var channel = builder.nextModel();
				channel.name = model.name + nextPattern("/action_{0}");
				model.channels.add(channel);
			}

		}
	}

	/**
	 * Create a model from an entity.
	 *
	 * @param entity to get the information.
	 *
	 * @return the model with the data of the entity.
	 */
	public static Component from(ComponentEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var component = new Component();
			component.apiVersion = entity.apiVersion;
			component.channels = entity.channels;
			component.description = entity.description;
			component.id = entity.id;
			component.name = entity.name;
			component.since = entity.since;
			component.type = entity.type;
			component.version = entity.version;
			return component;
		}

	}

}
