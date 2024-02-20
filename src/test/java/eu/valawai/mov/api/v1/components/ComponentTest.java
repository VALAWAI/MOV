/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link Component}.
 *
 * @see Component
 *
 * @author VALAWAI
 */
public class ComponentTest extends ModelTestCase<Component> {

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

		model.id = nextObjectId();
		model.name = nextPattern("Name of component {0}");
		model.description = nextPattern("Description of component {0}");
		model.version = nextPattern("{0}.{1}.{2}", 3);
		model.type = next(ComponentType.values());
		model.since = rnd().nextLong();
		model.apiVersion = nextPattern("{0}.{1}.{2}", 3);

		final var max = ValueGenerator.rnd().nextInt(0, 5);
		if (max > 0) {

			model.channels = new ArrayList<>();
			final var builder = new ChannelSchemaTest();
			for (var i = 0; i < max; i++) {

				final var channel = builder.nextModel();
				model.channels.add(channel);
			}

		}
	}

}
