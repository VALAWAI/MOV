/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link Point}.
 *
 * @see Point
 *
 * @author VALAWAI
 */
public class PointTest extends ModelTestCase<Point> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point createEmptyModel() {

		return new Point();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(Point model) {

		model.x = ValueGenerator.rnd().nextDouble(0.0d, 400.0d);
		model.y = ValueGenerator.rnd().nextDouble(0.0d, 400.0d);
	}

}
