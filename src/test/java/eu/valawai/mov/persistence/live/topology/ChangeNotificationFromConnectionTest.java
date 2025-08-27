/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link ChangeNotificationFromConnection}.
 *
 * @see ChangeNotificationFromConnection
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ChangeNotificationFromConnectionTest extends MovPersistenceTestCase {

	/**
	 * Check that cannot do an action over an undefined connection.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} an undefined connection.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverUndefinedConnection(TopologyAction action) {

		final var connectionId = TopologyConnectionEntities.undefined();
		final var target = new TopologyNodeTest().nextModel();
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connectionId)
				.withNode(target).withAction(action).execute());
		assertFalse(result);
	}

	/**
	 * Check that cannot do an action over a connection without notifications.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over a connection without notifications.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverConnectionWithoutNotifications(TopologyAction action) {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(0);
		final var target = new TopologyNodeTest().nextModel();
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(action).execute());
		assertFalse(result);
	}

	/**
	 * Check that cannot do an action over an undefined notification.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over an undefined notification.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverUndefinedtNotifications(TopologyAction action) {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		var target = new TopologyNodeTest().nextModel();
		NOTIFICATIONS: while (connection.notifications != null) {

			for (final var notification : connection.notifications) {

				if (notification.node.equals(target)) {

					target = new TopologyNodeTest().nextModel();
					continue NOTIFICATIONS;
				}
			}
			break;

		}
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(action).execute());
		assertFalse(result);
	}

	/**
	 * Check that cannot do an action over a deleted connection.
	 *
	 * @param action that can not be done.
	 */
	@ParameterizedTest(name = "Should not do action {0} over a deleted connection.")
	@EnumSource(TopologyAction.class)
	public void shouldNotDoActionOverDeletedConnection(TopologyAction action) {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = ValueGenerator.next(connection.notifications).node;
		connection.deletedTimestamp = ValueGenerator.nextPastTime();
		this.assertItemNotNull(connection.update());

		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(action).execute());
		assertFalse(result);

	}

	/**
	 * Check that enable notification.
	 */
	@Test
	public void shouldEanableNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = false;
		this.assertItemNotNull(connection.update());

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(TopologyAction.ENABLE).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertTrue(updated.notifications.get(1).enabled);

	}

	/**
	 * Check that disable notification.
	 */
	@Test
	public void shouldDisableNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = true;
		this.assertItemNotNull(connection.update());

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(TopologyAction.DISABLE).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertFalse(updated.notifications.get(1).enabled);

	}

	/**
	 * Check that remove notification.
	 */
	@Test
	public void shouldRemoveNotification() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection(3);
		final TopologyNode target = connection.notifications.get(1).node;
		connection.notifications.get(1).enabled = true;
		this.assertItemNotNull(connection.update());

		final var now = TimeManager.now();
		final var result = this.assertItemNotNull(ChangeNotificationFromConnection.fresh().withConnection(connection.id)
				.withNode(target).withAction(TopologyAction.REMOVE).execute());
		assertTrue(result);

		final TopologyConnectionEntity updated = this
				.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= updated.updateTimestamp);
		assertThat(updated.notifications, hasSize(2));
		assertThat(updated.notifications, not(hasItem(connection.notifications.get(1))));

	}

}
