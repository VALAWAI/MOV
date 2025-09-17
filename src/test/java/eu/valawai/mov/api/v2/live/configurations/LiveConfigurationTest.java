/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.configurations;

import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link LiveConfiguration}.
 *
 * @see LiveConfiguration
 *
 * @author VALAWAI
 */
public class LiveConfigurationTest extends ModelTestCase<LiveConfiguration> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LiveConfiguration createEmptyModel() {

		return new LiveConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(LiveConfiguration model) {

		model.topologyId = ValueGenerator.nextObjectId();
		model.registerComponent = ValueGenerator.next(TopologyBehavior.values());
		model.createConnection = ValueGenerator.next(TopologyBehavior.values());
	}

}
