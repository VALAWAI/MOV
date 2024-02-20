/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link MinComponentPage}.
 *
 * @see MinComponentPage
 *
 * @author VALAWAI
 */
public class MinComponentPageTest extends ModelTestCase<MinComponentPage> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinComponentPage createEmptyModel() {

		return new MinComponentPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(MinComponentPage model) {

		model.offset = rnd().nextInt();
		model.total = rnd().nextInt();
		final var max = rnd().nextInt(0, 11);
		if (max > 0) {

			model.components = new ArrayList<>();
			final var builder = new MinComponentTest();
			for (var i = 0; i < max; i++) {

				final var log = builder.nextModel();
				model.components.add(log);

			}
		}

	}

}
