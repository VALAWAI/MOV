/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link ComponentPlayload}.
 *
 * @see ComponentPlayload
 *
 * @author VALAWAI
 */
public class ComponentPlayloadTest extends PayloadTestCase<ComponentPlayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentPlayload createEmptyModel() {

		return new ComponentPlayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillIn(ComponentPlayload model) {

		model.componentId = ValueGenerator.nextObjectId();
	}

}
