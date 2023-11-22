/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link ComponentPage}.
 *
 * @see ComponentPage
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ComponentPageTest extends ModelTestCase<ComponentPage> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentPage createEmptyModel() {

		return new ComponentPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentPage nextModel() {

		final var model = this.createEmptyModel();
		model.offset = rnd().nextInt();
		model.total = rnd().nextInt();
		final var max = rnd().nextInt(0, 11);
		if (max > 0) {

			model.components = new ArrayList<>();
			final var builder = new ComponentTest();
			for (var i = 0; i < max; i++) {

				final var log = builder.nextModel();
				model.components.add(log);

			}
		}
		return model;
	}

}
