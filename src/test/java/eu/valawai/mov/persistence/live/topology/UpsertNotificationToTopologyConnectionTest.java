/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link UpsertNotificationToTopologyConnection}.
 *
 * @see UpsertNotificationToTopologyConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class UpsertNotificationToTopologyConnectionTest extends MovPersistenceTestCase {

	/**
	 * Should insert notification when no notification is defined.
	 */
	@Test
	public void shouldInsertInEmptyNotifications() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		if (connection.notifications != null) {

			connection.notifications = null;
			this.assertItemNotNull(connection.update());
		}
		final var notification = new TopologyConnectionNotificationTest().nextModel();

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(notification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(1));
		assertThat(updated.notifications.get(0), is(notification));
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

	/**
	 * Should insert undefined notification when no notification is defined.
	 */
	@Test
	public void shouldInsertUndefinedNotifications() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		if (connection.notifications == null) {

			connection.notifications = new ArrayList<>();

		}
		final var notification = new TopologyConnectionNotificationTest().nextModel();
		for (final var defined : connection.notifications) {

			while (defined.node.equals(notification.node)) {

				defined.node = new TopologyNodeTest().nextModel();
			}
		}

		while (connection.notifications.size() < 3) {

			final var defined = new TopologyConnectionNotificationTest().nextModel();
			if (!defined.node.equals(notification.node)) {

				connection.notifications.add(defined);
			}
		}

		this.assertItemNotNull(connection.update());

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(notification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(connection.notifications.size() + 1));
		assertThat(updated.notifications, hasItem(notification));
		for (final var defined : connection.notifications) {

			assertThat(updated.notifications, hasItem(defined));
		}
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

	/**
	 * Should update the existing notification.
	 */
	@Test
	public void shouldUpdateExistingNotifications() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		if (connection.notifications == null) {

			connection.notifications = new ArrayList<>();

		}

		while (connection.notifications.size() < 3) {

			final var notification = new TopologyConnectionNotificationTest().nextModel();
			connection.notifications.add(notification);
		}

		this.assertItemNotNull(connection.update());

		final var notification = new TopologyConnectionNotificationTest().nextModel();
		notification.node = connection.notifications.get(1).node;

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(notification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(connection.notifications.size()));
		assertThat(updated.notifications, hasItem(notification));

		for (final var defined : connection.notifications) {

			if (defined.node.equals(notification.node)) {

				assertThat(updated.notifications, not(hasItem(defined)));

			} else {

				assertThat(updated.notifications, hasItem(defined));
			}
		}
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

	/**
	 * Should not update the existing notification.
	 */
	@Test
	public void shouldNotUpdateExistingNotifications() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		if (connection.notifications == null) {

			connection.notifications = new ArrayList<>();

		}

		while (connection.notifications.size() < 3) {

			final var notification = new TopologyConnectionNotificationTest().nextModel();
			connection.notifications.add(notification);
		}

		this.assertItemNotNull(connection.update());

		final var notification = ValueGenerator.next(connection.notifications);

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(notification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(connection.notifications.size()));
		for (final var defined : connection.notifications) {

			assertThat(updated.notifications, hasItem(defined));
		}
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

	/**
	 * Should add notification to same component different channel name.
	 */
	@Test
	public void shouldAddNotificationToSameComponentDiferentChannelName() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		connection.notifications = new ArrayList<>();
		final var notification = new TopologyConnectionNotificationTest().nextModel();
		connection.notifications.add(notification);

		this.assertItemNotNull(connection.update());

		var newNotification = new TopologyConnectionNotificationTest().nextModel();
		while (notification.node.channelName.equals(newNotification.node.channelName)) {

			newNotification = new TopologyConnectionNotificationTest().nextModel();
		}
		newNotification.node.componentId = notification.node.componentId;

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(newNotification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(2));
		assertThat(updated.notifications, hasItem(notification));
		assertThat(updated.notifications, hasItem(newNotification));
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

	/**
	 * Should add notification to same channel name different component.
	 */
	@Test
	public void shouldAddNotificationToSameChannelNameDifferentComponentId() {

		final var connection = TopologyConnectionEntities.minTopologyConnections(1).get(0);
		connection.notifications = new ArrayList<>();
		final var notification = new TopologyConnectionNotificationTest().nextModel();
		connection.notifications.add(notification);

		this.assertItemNotNull(connection.update());

		var newNotification = new TopologyConnectionNotificationTest().nextModel();
		while (notification.node.componentId.equals(newNotification.node.componentId)) {

			newNotification = new TopologyConnectionNotificationTest().nextModel();
		}
		newNotification.node.channelName = notification.node.channelName;

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(UpsertNotificationToTopologyConnection.fresh()
				.withConnection(connection.id).witNotification(newNotification).execute());
		assertThat(result, is(true));

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertThat(updated.notifications, is(notNullValue()));
		assertThat(updated.notifications, hasSize(2));
		assertThat(updated.notifications, hasItem(notification));
		assertThat(updated.notifications, hasItem(newNotification));
		assertThat(updated.updateTimestamp, is(greaterThanOrEqualTo(now)));
	}

}
