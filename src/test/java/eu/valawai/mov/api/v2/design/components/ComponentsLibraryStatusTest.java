/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link ComponentsLibraryStatus}.
 *
 * @see ComponentsLibraryStatus
 *
 * @author VALAWAI
 */
public class ComponentsLibraryStatusTest extends ModelTestCase<ComponentsLibraryStatus> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentsLibraryStatus createEmptyModel() {

		return new ComponentsLibraryStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ComponentsLibraryStatus model) {

		model.oldestComponentTimestamp = ValueGenerator.nextPastTime();
		model.newestComponentTimestamp = ValueGenerator.nextPastTime();
		model.componentCount = ValueGenerator.rnd().nextLong(1, 100);
	}

}
