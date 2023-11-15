/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.help;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link Info}.
 *
 * @see Info
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class InfoTest extends ModelTestCase<Info> {

	/**
	 * {@inheritDoc}
	 *
	 * @see Info#Info()
	 */
	@Override
	public Info createEmptyModel() {

		return new Info();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Info nextModel() {

		final var model = this.createEmptyModel();
		model.name = nextPattern("Name of {0}");
		model.profile = next("prod", "dev", "test");
		model.version = nextPattern("{0}.{1}.{2}", 3);
		return model;
	}

}
