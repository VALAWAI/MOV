/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.PayloadTestCase;

/**
 * Test the {@link QueryComponentsPayload}.
 *
 * @see QueryComponentsPayload
 *
 * @author VALAWAI
 */
public class QueryComponentsPayloadTest extends PayloadTestCase<QueryComponentsPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryComponentsPayload createEmptyModel() {

		return new QueryComponentsPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(QueryComponentsPayload payload) {

		payload.id = nextUUID().toString();
		payload.pattern = nextPattern("pattern {0}");
		payload.type = next(ComponentType.values()).name();
		payload.order = next("type", "-type", "+name", "-name", "+description", "-description", "since", "-since");
		payload.offset = rnd().nextInt(0, 11);
		payload.limit = rnd().nextInt(1, 21);
	}

}
