/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link Component}.
 *
 * @see Component
 *
 * @author UDT-IA, IIIA-CSIC
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
	public Component nextModel() {

		final var model = this.createEmptyModel();
		model.id = nextObjectId();
		model.name = nextPattern("Name of component {0}");
		model.description = nextPattern("Description of component {0}");
		model.version = nextPattern("{0}.{1}.{2}", 3);
		model.type = next(ComponentType.values());
		model.since = rnd().nextLong();
		return model;
	}

}
