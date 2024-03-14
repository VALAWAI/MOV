/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link MinConnectionPage}.
 *
 * @see MinConnectionPage
 *
 * @author VALAWAI
 */
public class MinConnectionPageTest extends ModelTestCase<MinConnectionPage> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinConnectionPage createEmptyModel() {

		return new MinConnectionPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(MinConnectionPage model) {

		model.offset = rnd().nextInt();
		model.total = rnd().nextInt();
		final var max = rnd().nextInt(0, 11);
		if (max > 0) {

			model.connections = new ArrayList<>();
			final var builder = new MinConnectionTest();
			for (var i = 0; i < max; i++) {

				final var log = builder.nextModel();
				model.connections.add(log);

			}
		}

	}

}
