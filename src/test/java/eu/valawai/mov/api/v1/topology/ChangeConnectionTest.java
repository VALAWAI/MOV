/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;

import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.events.topology.TopologyAction;

/**
 * Test the {@link ChangeConnection}.
 *
 * @see ChangeConnection
 *
 * @author VALAWAI
 */
public class ChangeConnectionTest extends ModelTestCase<ChangeConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChangeConnection createEmptyModel() {

		return new ChangeConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ChangeConnection model) {

		model.action = next(TopologyAction.values());
		model.connectionId = nextObjectId();
	}

}
