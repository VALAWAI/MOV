/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link ConnectionToCreate}.
 *
 * @see ConnectionToCreate
 *
 * @author VALAWAI
 */
public class ConnectionToCreateTest extends ModelTestCase<ConnectionToCreate> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionToCreate createEmptyModel() {

		return new ConnectionToCreate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ConnectionToCreate model) {

		model.sourceComponent = nextObjectId();
		model.sourceChannel = nextPattern("valawai/channel_{0}");
		model.targetComponent = nextObjectId();
		model.targetChannel = nextPattern("valawai/channel_{0}");
		model.enabled = flipCoin();
	}

}
