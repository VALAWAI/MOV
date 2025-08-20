/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link TopologyConnectionNotification}.
 *
 * @see TopologyConnectionNotification
 *
 * @author VALAWAI
 */
public class TopologyConnectionNotificationTest extends ModelTestCase<TopologyConnectionNotification> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnectionNotification createEmptyModel() {

		return new TopologyConnectionNotification();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnectionNotification model) {

		model.node = new TopologyNodeTest().nextModel();
		model.enabled = ValueGenerator.rnd().nextBoolean();
		model.notificationMessageConverterJSCode = """
				function convert(a){

					return a;
				}
				export {convert};
				""" + "\n" + ValueGenerator.nextPattern("// code {0}");
	}

}
