/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.api.v1.components.PayloadSchemaTestCase.nextPayloadSchema;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link ChannelSchema}.
 *
 * @see ChannelSchema
 *
 * @author VALAWAI
 */
public class ChannelSchemaTest extends ModelTestCase<ChannelSchema> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelSchema createEmptyModel() {

		return new ChannelSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ChannelSchema model) {

		model.id = next("valawai/channel_{0}");
		model.description = next("Description of the channel {0}");
		if (flipCoin()) {

			model.subscribe = nextPayloadSchema();

		} else {

			model.publish = nextPayloadSchema();

		}
	}

}
