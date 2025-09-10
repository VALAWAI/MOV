/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MOVConfiguration;
import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.BasicPayloadFormat;
import eu.valawai.mov.api.v1.components.BasicPayloadSchema;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntities;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Check the {@link RegisterComponentManager} when the behavior is
 * {@link TopologyBehavior#APPLY_TOPOLOGY}.
 *
 * @see RegisterComponentManager
 * @see TopologyBehavior#APPLY_TOPOLOGY
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RegisterComponentManagerApplyTopologyTest extends RegisterComponentManagerTestCase {

	/**
	 * The payload to register the component 7.
	 */
	private final RegisterComponentPayload registerComponentSevenPayload = this.registerComponentSevenPayload();

	/**
	 * The topology definition o use in the tests.
	 */
	private final TopologyGraphEntity topology = this.loadTopologyGraphForTest();

	/**
	 * Set auto discover as default.
	 */
	@BeforeEach
	public void setApplyTopology() {

		ComponentEntities.clear();
		TopologyConnectionEntities.clear();

		this.assertItemNotNull(this.configuration.setProperty(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME,
				TopologyBehavior.APPLY_TOPOLOGY.name()));
		this.assertItemNotNull(this.configuration.setProperty(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME,
				TopologyBehavior.APPLY_TOPOLOGY.name()));
		this.assertItemNotNull(RegisterComponentManagerApplyTopologyTest.this.configuration
				.setProperty(MOVConfiguration.TOPOLOGY_ID_NAME, this.topology.id.toHexString()));

	}

	/**
	 * Load a topology graph for use in the tests.
	 */
	private TopologyGraphEntity loadTopologyGraphForTest() {

		final var topologyJson = this.loadResourceContent("topology.json");
		final var topology = Json.decodeValue(topologyJson, TopologyGraphEntity.class);
		final var created = new HashMap<String, ComponentDefinitionEntity>();
		for (final var node : topology.nodes) {

			ComponentType type = ComponentType.C2;
			if (node.outputs != null && node.outputs.size() > 0) {

				type = ComponentType.C0;
				if (node.outputs.get(0).sourceChannel.contains("/c1/")) {

					type = ComponentType.C1;
				}
			}
			final var componentDef = ComponentDefinitionEntities.nextComponentDefinitionWithType(type);
			if (componentDef.channels == null) {

				componentDef.channels = new ArrayList<>();

			} else if (type == ComponentType.C1) {

				componentDef.channels = ComponentBuilder
						.fromAsyncapi(this.registerComponentSevenPayload.asyncapiYaml).channels;

			}
			node.componentRef = componentDef.id;
			created.put(node.tag, componentDef);
		}

		for (final var node : topology.nodes) {

			if (node.outputs != null && !node.outputs.isEmpty()) {

				final var source = created.get(node.tag);
				for (final var output : node.outputs) {

					final var sourceChannel = new ChannelSchema();
					sourceChannel.name = output.sourceChannel;
					final var sourcePayload = new ObjectPayloadSchema();
					sourcePayload.properties.put("data", BasicPayloadSchema.with(BasicPayloadFormat.STRING));
					sourceChannel.publish = sourcePayload;
					source.channels.add(sourceChannel);

					final var target = created.get(output.targetTag);
					final var targetChannel = new ChannelSchema();
					targetChannel.name = output.targetChannel;
					final var targetPayload = new ObjectPayloadSchema();
					targetPayload.properties.put("data", BasicPayloadSchema.with(BasicPayloadFormat.STRING));
					targetChannel.subscribe = targetPayload;
					target.channels.add(targetChannel);

					if (output.notifications != null && !output.notifications.isEmpty()) {

						for (final var notification : output.notifications) {

							final var targetnotification = created.get(notification.targetTag);
							final var targetnotificationChannel = new ChannelSchema();
							targetnotificationChannel.name = notification.targetChannel;
							final var targetnotificationPayload = new ObjectPayloadSchema();
							targetnotificationPayload.properties.put("data",
									BasicPayloadSchema.with(BasicPayloadFormat.STRING));
							targetnotificationChannel.subscribe = targetnotificationPayload;
							targetnotification.channels.add(targetnotificationChannel);

						}
					}
				}
			}
		}

		for (final var componentDef : created.values()) {

			this.assertItemNotNull(componentDef.update());
		}

		this.assertItemNotNull(topology.persist());
		return topology;
	}

	/**
	 * The payload to register the component 7.
	 */
	private RegisterComponentPayload registerComponentSevenPayload() {

		final var payload = new RegisterComponentPayload();
		payload.name = "c1_component_7";
		payload.version = "2.0.0";
		payload.type = ComponentType.C1;
		payload.asyncapiYaml = this.loadResourceContent("component_7.asyncapi.yml");
		return payload;
	}

	/**
	 * Create a component with the specified reference.
	 *
	 * @param componentRefId identifier of the reference to the component to create.
	 *
	 * @return the created component.
	 */
	private ComponentEntity createComponentForRef(ObjectId componentRefId) {

		final ComponentDefinitionEntity componentDefinition = this
				.assertItemNotNull(ComponentDefinitionEntity.findById(componentRefId));

		final var component = ComponentEntities.nextComponent();
		component.type = componentDefinition.type;
		if (component.channels == null) {

			component.channels = new ArrayList<>();
		}
		component.channels.addAll(componentDefinition.channels);
		ValueGenerator.shuffle(component.channels);
		this.assertItemNotNull(component.update());

		return component;
	}

	/**
	 * Check that the component 7 is been registered.
	 */
	protected ComponentEntity assertRegisterComponentSeven() {

		final var expectedComponentscount = this.assertItemNotNull(ComponentEntity.count()) + 1;
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2,
				() -> this.assertPublish(this.registerComponentQueueName, this.registerComponentSevenPayload));

		final var countComponentsAfter = this.assertItemNotNull(ComponentEntity.count());
		assertThat(countComponentsAfter, is(expectedComponentscount));

		final ComponentEntity component = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.descending("_id")).firstResult());
		assertThat(component.since, is(greaterThanOrEqualTo(now)));
		assertThat(component.name, is(this.registerComponentSevenPayload.name));
		assertThat(component.type, is(ComponentType.C1));
		assertThat(component.version, is(this.registerComponentSevenPayload.version));
		assertThat(component.apiVersion, is("1.0.0"));
		assertThat(component.finishedTime, is(nullValue()));
		assertThat(component.channels, is(not(nullValue())));

		return component;

	}

	/**
	 * Should register component without any connection.
	 */
	@Test
	public void shouldOnlyRegisterComponent() {

		this.assertRegisterComponentSeven();

	}

	/**
	 * Wait until the number of connections is the expected.
	 *
	 * @param expectedConnectionCount the number of connection that are expected.
	 */
	private void waitUntilConnectionCountIs(final long expectedConnectionCount) {

		this.waitUntil(() -> this.assertItemNotNull(TopologyConnectionEntity.count("deletedTimestamp is null")),
				count -> count == expectedConnectionCount);
	}

	/**
	 * Check that the component 7 is been registered and create the number of
	 * connections.
	 *
	 * @param offset the number of connection to create.
	 *
	 * @return the registered component.
	 */
	protected ComponentEntity assertRegisterComponentSevenAndCreateConnection(long offset) {

		final var expectedConnectionCount = this
				.assertItemNotNull(TopologyConnectionEntity.count("deletedTimestamp is null")) + offset;
		final var component = this.assertRegisterComponentSeven();
		this.waitUntilConnectionCountIs(expectedConnectionCount);
		return component;

	}

	/**
	 * Return the last created connections.
	 *
	 * @param limit number of the last connections to get.
	 *
	 * @return the last specified number of the last connections.
	 */
	private List<TopologyConnectionEntity> getLastCreatedConnections(int limit) {

		final List<TopologyConnectionEntity> connections = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).page(0, limit).list());
		assertThat(connections, is(not(nullValue())));
		assertThat(connections, is(hasSize(limit)));
		return connections;
	}

	/**
	 * Create a connection when the registered component is a source.
	 */
	@Test
	public void shouldCreateOnlyConnectionAsSource() {

		final var target = this.createComponentForRef(this.topology.nodes.get(8).componentRef);

		final var source = this.assertRegisterComponentSevenAndCreateConnection(1);

		final var connection = this.getLastCreatedConnections(1).get(0);
		assertThat(connection.source, is(not(nullValue())));
		assertThat(connection.source.componentId, is(source.id));
		assertThat(connection.target, is(not(nullValue())));
		assertThat(connection.target.componentId, is(target.id));
		assertThat(connection.createTimestamp, is(greaterThanOrEqualTo(source.since)));
		assertThat(connection.updateTimestamp, is(greaterThanOrEqualTo(source.since)));
		assertThat(connection.enabled, is(true));
		assertThat(connection.notifications, is(nullValue()));
		assertThat(connection.deletedTimestamp, is(nullValue()));
		assertThat(connection.targetMessageConverterJSCode, is(nullValue()));

	}

	/**
	 * Create multiple connection when the registered component is a source.
	 */
	@Test
	public void shouldCreateMultipleConnectionAsSource() {

		final var targets = new ArrayList<ComponentEntity>();
		final var notificationNodes = new ArrayList<ComponentEntity>();
		final var expectedConnections = 6;
		for (var i = 7; i < this.topology.nodes.size(); i++) {

			final var node = this.topology.nodes.get(i);
			final var target = this.createComponentForRef(node.componentRef);
			if (i < 13) {

				targets.add(target);

			} else {

				notificationNodes.add(target);
			}
		}

		final var source = this.assertRegisterComponentSevenAndCreateConnection(expectedConnections);

		final var lastConnections = this.getLastCreatedConnections(expectedConnections);
		for (final var connection : lastConnections) {

			ComponentEntity target = null;
			for (var i = 0; i < targets.size(); i++) {

				final var registered = targets.get(i);
				if (connection.target != null && registered.id.equals(connection.target.componentId)) {

					target = registered;
					break;
				}
			}

			assertThat(target, is(not(nullValue())));

			assertThat(connection.source, is(not(nullValue())));
			assertThat(connection.source.componentId, is(source.id));
			assertThat(connection.target, is(not(nullValue())));
			assertThat(connection.target.componentId, is(target.id));
			assertThat(connection.createTimestamp, is(greaterThanOrEqualTo(source.since)));
			assertThat(connection.updateTimestamp, is(greaterThanOrEqualTo(source.since)));
			assertThat(connection.enabled, is(true));
			assertThat(connection.notifications, is(nullValue()));
			assertThat(connection.deletedTimestamp, is(nullValue()));
			assertThat(connection.targetMessageConverterJSCode, is(nullValue()));

		}

		for (final var nodeNotification : notificationNodes) {

			final var count = this.assertItemNotNull(TopologyConnectionEntity.count(
					"source.componentId = ?1 or target.componentId = ?1 or notification.node.componentId = ?1",
					nodeNotification.id));
			assertThat(count, is(0l));
		}

	}

	/**
	 * Create a connection when the registered component is a target.
	 */
	@Test
	public void shouldCreateOnlyConnectionAsTarget() {

		final var source = this.createComponentForRef(this.topology.nodes.get(0).componentRef);

		final var target = this.assertRegisterComponentSevenAndCreateConnection(1);

		final var connection = this.getLastCreatedConnections(1).get(0);
		assertThat(connection.source, is(not(nullValue())));
		assertThat(connection.source.componentId, is(source.id));
		assertThat(connection.target, is(not(nullValue())));
		assertThat(connection.target.componentId, is(target.id));
		assertThat(connection.createTimestamp, is(greaterThanOrEqualTo(target.since)));
		assertThat(connection.updateTimestamp, is(greaterThanOrEqualTo(target.since)));
		assertThat(connection.enabled, is(true));
		assertThat(connection.notifications, is(nullValue()));
		assertThat(connection.deletedTimestamp, is(nullValue()));
		assertThat(connection.targetMessageConverterJSCode, is(nullValue()));

	}

	/**
	 * Create multiple connection when the registered component is a target.
	 */
	@Test
	public void shouldCreateMultipleConnectionAsSTarget() {

		final var sources = new ArrayList<ComponentEntity>();
		final var expectedConnections = 6;
		for (var i = 0; i < 7; i++) {

			final var node = this.topology.nodes.get(i);
			final var source = this.createComponentForRef(node.componentRef);
			sources.add(source);
		}

		final var target = this.assertRegisterComponentSevenAndCreateConnection(expectedConnections);

		final var lastConnections = this.getLastCreatedConnections(expectedConnections);
		for (final var connection : lastConnections) {

			ComponentEntity source = null;
			for (var i = 0; i < sources.size(); i++) {

				final var registered = sources.get(i);
				if (connection.source != null && registered.id.equals(connection.source.componentId)) {

					source = registered;
					break;
				}
			}

			assertThat(source, is(not(nullValue())));

			assertThat(connection.source, is(not(nullValue())));
			assertThat(connection.source.componentId, is(source.id));
			assertThat(connection.target, is(not(nullValue())));
			assertThat(connection.target.componentId, is(target.id));
			assertThat(connection.createTimestamp, is(greaterThanOrEqualTo(source.since)));
			assertThat(connection.updateTimestamp, is(greaterThanOrEqualTo(source.since)));
			assertThat(connection.enabled, is(true));
			assertThat(connection.notifications, is(nullValue()));
			assertThat(connection.deletedTimestamp, is(nullValue()));
			assertThat(connection.targetMessageConverterJSCode, is(nullValue()));

		}

	}

}
