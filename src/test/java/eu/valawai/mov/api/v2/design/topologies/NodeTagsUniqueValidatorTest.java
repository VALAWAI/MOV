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

/**
 * Test the {@link NodeTagsUniqueValidator}.
 *
 * @see NodeTagsUniqueValidator
 *
 * @author VALAWAI
 */
public class NodeTagsUniqueValidatorTest {

	/**
	 * The validator to test.
	 */
	private final NodeTagsUniqueValidator validator = new NodeTagsUniqueValidator();

	/**
	 * Should a {@code null} topology be valid.
	 */
	@Test
	public void shouldNullTopologyBeValid() {

		assertTrue(this.validator.isValid(null, null));
	}

	/**
	 * Should a {@code null} nodes in topology be valid.
	 */
	@Test
	public void shouldNullNodesInTopologyBeValid() {

		final var topology = new Topology();
		assertTrue(this.validator.isValid(topology, null));
	}

	/**
	 * Should a duplicated nodes in topology be invalid.
	 */
	@Test
	public void shouldDuplicatedNodesInTopologyBeInvalid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		topology.nodes.add(new TopologyNode());
		topology.nodes.add(new TopologyNode());
		topology.nodes.add(new TopologyNode());
		for (final var node : topology.nodes) {
			node.tag = "test";
		}
		assertFalse(this.validator.isValid(topology, null));
	}

	/**
	 * Should non duplicated nodes in topology be valid.
	 */
	@Test
	public void shouldNodesInTopologyBeValid() {

		final var topology = new Topology();
		topology.nodes = new ArrayList<>();
		topology.nodes.add(new TopologyNode());
		topology.nodes.add(new TopologyNode());
		topology.nodes.add(new TopologyNode());
		for (var i = 0; i < topology.nodes.size(); i++) {
			final var node = topology.nodes.get(i);
			node.tag = "test_" + i;
		}
		assertTrue(this.validator.isValid(topology, null));
	}

}
