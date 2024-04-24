/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link AddC2SubscriptionToTopologyConnection}.
 *
 * @see AddC2SubscriptionToTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class AddC2SubscriptionToTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Check that not add a duplicated subscription.
	 */
	@Test
	public void shouldNotAddDuplicatedSubscription() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.c2Subscriptions = new ArrayList<>();
		final var node = new TopologyNodeTest().nextModel();
		connection.c2Subscriptions.add(node);
		this.assertItemNotNull(connection.update());

		for (var i = 0; i < 10; i++) {

			final var result = this.assertExecutionNotNull(AddC2SubscriptionToTopologyConnection.fresh()
					.withConnection(connection.id).withComponent(node.componentId).withChannel(node.channelName));
			assertFalse(result);

			final TopologyConnectionEntity updated = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertEquals(connection.c2Subscriptions, updated.c2Subscriptions);
		}
	}

	/**
	 * Check that add a subscription.
	 */
	@Test
	public void shouldAddSubscription() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.c2Subscriptions = new ArrayList<>();

		for (var i = 0; i < 10; i++) {

			final var node = new TopologyNodeTest().nextModel();
			final var now = TimeManager.now();
			final var result = this.assertExecutionNotNull(AddC2SubscriptionToTopologyConnection.fresh()
					.withConnection(connection.id).withComponent(node.componentId).withChannel(node.channelName));
			assertTrue(result);

			connection.c2Subscriptions.add(node);
			final TopologyConnectionEntity updated = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertEquals(connection.c2Subscriptions, updated.c2Subscriptions);
			assertTrue(now <= updated.updateTimestamp);

		}
	}

}
