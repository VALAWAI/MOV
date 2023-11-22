/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
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
 * @author UDT-IA, IIIA-CSIC
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
	public ChannelSchema nextModel() {

		final var model = this.createEmptyModel();
		model.id = next("valawai/channel_{0}");
		model.description = next("Description of the channel {0}");
		if (flipCoin()) {

			model.subscribe = nextPayloadSchema();

		} else {

			model.publish = nextPayloadSchema();

		}
		return model;
	}

}
