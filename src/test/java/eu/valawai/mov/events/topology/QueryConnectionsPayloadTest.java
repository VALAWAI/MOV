/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;

import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link QueryConnectionsPayload}.
 *
 * @see QueryConnectionsPayload
 *
 * @author VALAWAI
 */
public class QueryConnectionsPayloadTest extends PayloadTestCase<QueryConnectionsPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryConnectionsPayload createEmptyModel() {

		return new QueryConnectionsPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(QueryConnectionsPayload payload) {

		payload.id = nextUUID().toString();
		payload.sourceChannelName = nextPattern("pattern {0}");
		payload.sourceComponentId = nextObjectId().toHexString();
		payload.targetChannelName = nextPattern("pattern {0}");
		payload.targetComponentId = nextObjectId().toHexString();
		payload.order = next("createTimestamp", "-createTimestamp", "+updateTimestamp", "-updateTimestamp",
				"+source.componentId", "-source.componentId", "source.channelName", "-source.channelName",
				"+target.componentId", "-target.componentId", "target.channelName", "-target.channelName");
		payload.offset = rnd().nextInt(0, 11);
		payload.limit = rnd().nextInt(1, 21);
	}

}
