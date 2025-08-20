/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.api.v1.components.ComponentType;

/**
 * Test the {@link TopologyNode}.
 *
 * @see TopologyNode
 *
 * @author VALAWAI
 */
public class TopologyNodeTest extends ModelTestCase<TopologyNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyNode createEmptyModel() {

		return new TopologyNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyNode model) {

		model.componentId = ValueGenerator.nextObjectId();
		model.channelName = ValueGenerator.nextPattern("valawai/CX_source_name_{0}");
	}

	/**
	 * Should not infer the component type from channel.
	 *
	 * @param channel that can not infer the component type.
	 */
	@ParameterizedTest(name = "Should not infer the component type from channel {0}")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "valawai/c4/do_something", "valawai/do_something/data/msg", "C1/do_something/data/chat" })
	public void shoudInferComponentType(String channel) {

		final var node = new TopologyNode();
		node.channelName = channel;
		assertThat(node.inferComponentType(), is(nullValue()));
	}

	/**
	 * Should infer the component type from channel.
	 *
	 * @param typeName to infer.
	 * @param channel  to infer from.
	 */
	@ParameterizedTest(name = "Should infer the component type {0} from channel {1}")
	@CsvSource({ "C0,valawai/c0/do_something/action/event", "C1,valawai/c1/do_something/data/msg",
			"C2,valawai/c2/do_something/control/parameters" })
	public void shoudInferComponentType(String typeName, String channel) {

		final var node = new TopologyNode();
		node.channelName = channel;
		final var expectedType = ComponentType.valueOf(typeName);
		assertThat(node.inferComponentType(), is(equalTo(expectedType)));
	}

	/**
	 * Should not infer the component name from channel.
	 *
	 * @param channel that can not infer the component name.
	 */
	@ParameterizedTest(name = "Should not infer the component name from channel {0}")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "valawai/c4/do_something", "valawai/do_something/data/msg", "C1/do_something/data/chat" })
	public void shoudInferComponentName(String channel) {

		final var node = new TopologyNode();
		node.channelName = channel;
		assertThat(node.inferComponentName(), is(nullValue()));
	}

	/**
	 * Should infer the component name from channel.
	 *
	 * @param expectedName to infer.
	 * @param channel      to infer from.
	 */
	@ParameterizedTest(name = "Should infer the component name {0} from channel {1}")
	@CsvSource({ "c0_do_something,valawai/c0/do_something/action/event",
			"c1_do_something,valawai/C1/do_something/data/msg",
			"c2_do_something,valawai/C2/do_something/control/parameters" })
	public void shoudInferComponentName(String expectedName, String channel) {

		final var node = new TopologyNode();
		node.channelName = channel;
		assertThat(node.inferComponentName(), is(equalTo(expectedName)));
	}

}
