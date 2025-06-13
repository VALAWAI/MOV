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
 * Test the {@link VersionInfo}.
 *
 * @see VersionInfo
 *
 * @author VALAWAI
 */
public class VersionInfoTest extends ModelTestCase<VersionInfo> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VersionInfo createEmptyModel() {

		return new VersionInfo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(VersionInfo model) {

		model.name = ValueGenerator.nextPattern("{0}.{1}.{2}", 3);
		model.since = ValueGenerator.nextPastTime();
	}

}
