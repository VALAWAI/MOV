/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.logs;

import static eu.valawai.mov.ValueGenerator.rnd;

import java.util.ArrayList;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link LogRecordPage}.
 *
 * @see LogRecordPage
 *
 * @author VALAWAI
 */
public class LogRecordPageTest extends ModelTestCase<LogRecordPage> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogRecordPage createEmptyModel() {

		return new LogRecordPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogRecordPage nextModel() {

		final var model = this.createEmptyModel();
		model.offset = rnd().nextInt();
		model.total = rnd().nextInt();
		final var max = rnd().nextInt(0, 11);
		if (max > 0) {

			model.logs = new ArrayList<>();
			final var builder = new LogRecordTest();
			for (var i = 0; i < max; i++) {

				final var log = builder.nextModel();
				model.logs.add(log);

			}
		}
		return model;
	}

}
