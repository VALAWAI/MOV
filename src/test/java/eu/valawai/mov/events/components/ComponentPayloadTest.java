/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.api.v1.components.ChannelSchemaTest;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.persistence.components.ComponentEntity;

/**
 * Test the {@link ComponentPayload}.
 *
 * @see ComponentPayload
 *
 * @author VALAWAI
 */
public class ComponentPayloadTest extends PayloadTestCase<ComponentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentPayload createEmptyModel() {

		return new ComponentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ComponentPayload payload) {

		payload.id = nextObjectId();
		payload.type = next(ComponentType.values());
		payload.name = nextPattern(payload.type.name().toLowerCase() + "_test_component_name_{0}");
		payload.description = nextPattern("Description of the component {0}");
		payload.version = nextPattern("{0}.{1}.{2}", 3);
		payload.apiVersion = nextPattern("{0}.{1}.{2}", 3);
		payload.since = nextPastTime();

		final int max = rnd().nextInt(0, 5);
		if (max > 0) {

			payload.channels = new ArrayList<>();
			final var builder = new ChannelSchemaTest();
			for (var i = 0; i < max; i++) {

				final var channel = builder.nextModel();
				payload.channels.add(channel);

			}
		}

	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static ComponentPayload from(ComponentEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new ComponentPayload();
			model.id = entity.id;
			model.type = entity.type;
			model.name = entity.name;
			model.description = entity.description;
			model.version = entity.version;
			model.apiVersion = entity.apiVersion;
			model.channels = entity.channels;
			model.since = entity.since;
			return model;

		}
	}

}
