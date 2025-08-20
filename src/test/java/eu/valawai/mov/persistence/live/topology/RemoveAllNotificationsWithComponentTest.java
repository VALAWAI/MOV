/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPastTime;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link RemoveAllNotificationsWithComponent}.
 *
 * @see RemoveAllNotificationsWithComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RemoveAllNotificationsWithComponentTest extends MovPersistenceTestCase {

	/**
	 * Should not remove for undefined component.
	 */
	@Test
	public void shouldNotRemoveForAnUndefinedComponent() {

		TopologyConnectionEntities.minTopologyConnections(100);
		final var componentId = nextObjectId();
		final var result = this
				.assertExecutionNotNull(RemoveAllNotificationsWithComponent.fresh().withComponent(componentId));
		assertEquals(0l, result);

	}

	/**
	 * Should not remove for a component that is not a c2.
	 */
	@Test
	public void shouldNotRemoveForANonC2() {

		TopologyConnectionEntities.minTopologyConnections(100);
		var component = ComponentEntities.nextComponent();
		while (component.type == ComponentType.C2) {

			component = ComponentEntities.nextComponent();
		}
		final var result = this
				.assertExecutionNotNull(RemoveAllNotificationsWithComponent.fresh().withComponent(component.id));
		assertEquals(0l, result);

	}

	/**
	 * Should remove subscription for one connection.
	 */
	@Test
	public void shouldRemoveAllForC2FromOneConnection() {

		var component = ComponentEntities.nextComponent();
		while (component.type != ComponentType.C2) {

			component = ComponentEntities.nextComponent();
		}
		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.notifications = new ArrayList<>();
		final var node = new TopologyNode();
		node.componentId = component.id;
		node.channelName = "undefined";
		final var notification = new TopologyConnectionNotification();
		notification.node = node;
		connection.notifications.add(notification);
		this.assertItemNotNull(connection.update());

		final var now = TimeManager.now();
		final var result = this
				.assertExecutionNotNull(RemoveAllNotificationsWithComponent.fresh().withComponent(component.id));
		assertEquals(1l, result);

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertEquals(Collections.EMPTY_LIST, updated.notifications);
	}

	/**
	 * Should not remove subscription for one deleted connection.
	 */
	@Test
	public void shouldNotRemoveAllForC2FromOneDeletedConnection() {

		var component = ComponentEntities.nextComponent();
		while (component.type != ComponentType.C2) {

			component = ComponentEntities.nextComponent();
		}
		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		connection.notifications = new ArrayList<>();
		final var node = new TopologyNode();
		node.componentId = component.id;
		node.channelName = "undefined";
		final var notification = new TopologyConnectionNotification();
		notification.node = node;
		connection.notifications.add(notification);
		connection.deletedTimestamp = nextPastTime();
		this.assertItemNotNull(connection.update());

		final var result = this
				.assertExecutionNotNull(RemoveAllNotificationsWithComponent.fresh().withComponent(component.id));
		assertEquals(0l, result);

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertEquals(connection.updateTimestamp, updated.updateTimestamp);
		assertEquals(connection.notifications, updated.notifications);
	}

	/**
	 * Should remove subscription for some connection.
	 */
	@Test
	public void shouldRemoveAllForC2FromMultipleConnections() {

		final List<ComponentEntity> c2s = new ArrayList<>();
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.type == ComponentType.C2) {

				c2s.add(component);
			}

		} while (c2s.size() < 10);
		final var c2 = c2s.get(0);

		final List<TopologyConnectionEntity> connections = new ArrayList<>();
		do {

			final var connection = TopologyConnectionEntities.nextTopologyConnection();

			Collections.shuffle(c2s);
			final var max = rnd().nextInt(1, c2s.size());
			final var subC2s = c2s.subList(0, max);
			connection.notifications = new ArrayList<>();
			var containsC2 = false;
			for (final var component : subC2s) {

				final var node = new TopologyNode();
				node.componentId = component.id;
				node.channelName = "undefined";
				final var notification = new TopologyConnectionNotification();
				notification.node = node;
				connection.notifications.add(notification);
				if (c2.id.equals(component.id)) {
					containsC2 = true;
				}
			}

			this.assertItemNotNull(connection.update());
			if (containsC2) {

				connections.add(connection);
			}

		} while (connections.size() < 23);

		final var now = TimeManager.now();
		final var result = this
				.assertExecutionNotNull(RemoveAllNotificationsWithComponent.fresh().withComponent(c2.id));
		assertEquals(connections.size(), result);

		for (final var connection : connections) {

			final TopologyConnectionEntity updated = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertTrue(now <= updated.updateTimestamp);
			final var iter = connection.notifications.iterator();
			while (iter.hasNext()) {

				final var subscription = iter.next();
				if (subscription.node.componentId.equals(c2.id)) {
					iter.remove();
					break;
				}
			}
			assertEquals(connection.notifications, updated.notifications);

		}
	}

}
