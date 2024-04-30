/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.topology.TopologyConnectionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetTopologyConnection}.
 *
 * @see GetTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Should get {@code null} without connection.
	 */
	@Test
	public void shouldGetNullConnectionForNoCompnentId() {

		this.assertItemNullOrFailed(GetTopologyConnection.fresh().execute());

	}

	/**
	 * Should get {@code null} with an undefined connection.
	 */
	@Test
	public void shouldGetNullConnectionForUndefinedConnection() {

		this.assertExecutionNull(GetTopologyConnection.fresh().withConnection(nextObjectId()));

	}

	/**
	 * Should get a connection.
	 */
	@Test
	public void shouldGetConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var result = this.assertExecutionNotNull(GetTopologyConnection.fresh().withConnection(connection.id));
		final var expected = TopologyConnectionTest.from(connection);
		assertEquals(expected, result);
	}

	/**
	 * Should get a connection with subscriptions.
	 */
	@Test
	public void shouldGetConnectionWithSubscriptions() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		if (connection.c2Subscriptions == null) {

			connection.c2Subscriptions = new ArrayList<>();
			final var max = rnd().nextInt(1, 23);
			do {

				final var component = ComponentEntities.nextComponent();
				if (component.type == ComponentType.C2 && component.channels != null && !component.channels.isEmpty()
						&& component.channels.get(0).subscribe != null) {

					final var subscription = new TopologyNode();
					subscription.componentId = component.id;
					subscription.channelName = component.channels.get(0).name;
					connection.c2Subscriptions.add(subscription);
				}

			} while (connection.c2Subscriptions.size() < max);
			this.assertItemNotNull(connection.update());

		}
		final var result = this.assertExecutionNotNull(GetTopologyConnection.fresh().withConnection(connection.id));
		final var expected = TopologyConnectionTest.from(connection);
		assertEquals(expected, result);
	}

}
