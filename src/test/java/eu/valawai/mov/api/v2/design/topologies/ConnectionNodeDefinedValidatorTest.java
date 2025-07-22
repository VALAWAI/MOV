/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.ValueGenerator;

/**
 * Test the {@link ConnectionNodeDefinedValidator}.
 *
 * @see ConnectionNodeDefinedValidator
 *
 * @author VALAWAI
 */
public class ConnectionNodeDefinedValidatorTest {

	/**
	 * The validator to test.
	 */
	private final ConnectionNodeDefinedValidator validator = new ConnectionNodeDefinedValidator();

	/**
	 * Should a {@code null} topology be valid.
	 */
	@Test
	public void shouldNullTopologyBeValid() {

		assertTrue(this.validator.isValid(null, null));
	}

	/**
	 * Should a {@code null} connection in topology be valid.
	 */
	@Test
	public void shouldNullConnectionsInTopologyBeValid() {

		final var topology = new Topology();
		assertTrue(this.validator.isValid(topology, null));
	}

	/**
	 * Should a {@code null} nodes for defined connection in topology be invalid.
	 */
	@Test
	public void shouldConnectionsInTopologyWithoutNodesBeInvalid() {

		final var topology = new Topology();
		topology.connections = new ArrayList<>();
		topology.connections.add(new TopologyConnectionTest().nextModel());
		assertFalse(this.validator.isValid(topology, null));
	}

	/**
	 * Should not be valid a topology with undefined source node tag.
	 */
	@Test
	public void shouldConnectionsdWithUndefinedSourceBeInvalid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		final var nodeBuilder = new TopologyNodeTest();
		for (var i = 0; i < 10; i++) {

			final var node = nodeBuilder.nextModel();
			topology.nodes.add(node);
		}

		topology.connections = new ArrayList<>();
		final var connectionBuilder = new TopologyConnectionTest();
		final var connection = connectionBuilder.nextModel();
		connection.source.nodeTag = ValueGenerator.nextPattern("undefined_{0}");
		connection.target.nodeTag = ValueGenerator.next(topology.nodes).tag;
		connection.notifications = null;
		topology.connections.add(connection);
		assertFalse(this.validator.isValid(topology, null));
	}

	/**
	 * Should not be valid a topology with undefined target node tag.
	 */
	@Test
	public void shouldConnectionsdWithUndefinedTargetBeInvalid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		final var nodeBuilder = new TopologyNodeTest();
		for (var i = 0; i < 10; i++) {

			final var node = nodeBuilder.nextModel();
			topology.nodes.add(node);
		}

		topology.connections = new ArrayList<>();
		final var connectionBuilder = new TopologyConnectionTest();
		final var connection = connectionBuilder.nextModel();
		connection.target.nodeTag = ValueGenerator.nextPattern("undefined_{0}");
		connection.source.nodeTag = ValueGenerator.next(topology.nodes).tag;
		connection.notifications = null;
		topology.connections.add(connection);
		assertFalse(this.validator.isValid(topology, null));
	}

	/**
	 * Should not be valid a topology with an undefined notification target node
	 * tag.
	 */
	@Test
	public void shouldConnectionsdWithUndefinedNotificationTargetBeInvalid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		final var nodeBuilder = new TopologyNodeTest();
		for (var i = 0; i < 10; i++) {

			final var node = nodeBuilder.nextModel();
			topology.nodes.add(node);
		}

		topology.connections = new ArrayList<>();
		final var connectionBuilder = new TopologyConnectionTest();
		final var connection = connectionBuilder.nextModel();
		connection.source.nodeTag = ValueGenerator.next(topology.nodes).tag;
		connection.target.nodeTag = ValueGenerator.next(topology.nodes).tag;
		connection.notifications = new ArrayList<>();
		connection.notifications.add(new TopologyConnectionNotificationTest().nextModel());
		connection.notifications.get(0).target.nodeTag = ValueGenerator.nextPattern("undefined_{0}");
		topology.connections.add(connection);
		assertFalse(this.validator.isValid(topology, null));
	}

	/**
	 * Should a topology with defined tags be valid.
	 */
	@Test
	public void shouldNodesDefinedBeValid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		final var nodeBuilder = new TopologyNodeTest();
		for (var i = 0; i < 10; i++) {

			final var node = nodeBuilder.nextModel();
			topology.nodes.add(node);
		}

		topology.connections = new ArrayList<>();
		final var connectionBuilder = new TopologyConnectionTest();
		for (var i = 0; i < 10; i++) {

			final var connection = connectionBuilder.nextModel();
			connection.source.nodeTag = topology.nodes.get(i % 10).tag;
			connection.target.nodeTag = topology.nodes.get((i + 1) % 10).tag;
			if (connection.notifications != null) {

				for (final var notification : connection.notifications) {

					notification.target.nodeTag = ValueGenerator.next(topology.nodes).tag;
				}
			}
			topology.connections.add(connection);
		}
		assertTrue(this.validator.isValid(topology, null));
	}

}
