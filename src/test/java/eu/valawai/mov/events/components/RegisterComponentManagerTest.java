/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.BasicPayloadFormat;
import eu.valawai.mov.api.v1.components.BasicPayloadSchema;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.components.ComponentTest;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import eu.valawai.mov.persistence.topology.TopologyNode;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link RegisterComponentManager}.
 *
 * @see RegisterComponentManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RegisterComponentManagerTest extends MovEventTestCase {

	/**
	 * The name of the queue to send the register component events.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.register_component.queue.name", defaultValue = "valawai/component/register")
	String registerComponentQueueName;

	/**
	 * Check that cannot register with an invalid payload.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidPayload() {

		final var payload = new RegisterComponentPayload();
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerComponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that the not register with an invalid AsyncAPI.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidAsyncAPI() {

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.asyncapiYaml += "channels:\n\tBad:\n\ttype: string";
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerComponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that can register a component without channels.
	 */
	@Test
	public void shouldNotRegisterComponentWithoutChannels() {

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.asyncapiYaml = nextPattern("""
				asyncapi: 2.6.0
				info:
				  title: Service {0}
				  version: {1}.{2}.{3}
				  description: This service is in charge of processing user signups
				channels:
				""", 4).trim().replaceAll("\\t", "");

		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerComponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));

	}

	/**
	 * Check that a component is created and is created a connection as the
	 * registered component as source.
	 */
	@Test
	public void shouldRegisterComponentAndCreateConnectionAsSource() {

		// The message to register the target component of the connection
		final var componentTypeIndex = rnd().nextInt(0, 3);
		final var componentName = nextPattern("component_{0}");
		final var apiVersion = nextPattern("{0}.{1}.{2}", 3);
		final var apiDescription = nextPattern("Description of the API {0}");
		final var actionName = nextPattern("source_action_{0}");
		final var fieldName = actionName + nextPattern("_field_{0}");
		final var sourceChannelName = MessageFormat.format("valawai/c{0}/{1}/data/{2}", componentTypeIndex,
				componentName, actionName);
		final var sourceChannelDescription = nextPattern("Description of the source channel {0}");

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.name = MessageFormat.format("c{0}_{1}", componentTypeIndex, componentName);
		payload.type = ComponentType.values()[componentTypeIndex];
		payload.asyncapiYaml = MessageFormat.format(
				this.loadAsyncapiResourceTemplate("component_to_register_and_be_source.pattern.yml"),
				componentTypeIndex, componentName, apiVersion, apiDescription, actionName, sourceChannelDescription,
				fieldName);

		// Create the component that will be the target of the connection
		ComponentEntity target = ComponentEntities.nextComponent();
		while (target.channels == null || target.channels.isEmpty()) {

			target = ComponentEntities.nextComponent();
		}
		ComponentEntities.minComponents(23);

		final var targetChannel = target.channels.get(0);
		target.channels.clear();
		target.channels.add(targetChannel);
		targetChannel.publish = null;
		final var expectedSchema = new ObjectPayloadSchema();
		expectedSchema.properties.put(fieldName, BasicPayloadSchema.with(BasicPayloadFormat.STRING));
		targetChannel.subscribe = expectedSchema;
		this.assertItemNotNull(target.update());

		final var countConnectionsBefore = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var countComponentsBefore = this.assertItemNotNull(ComponentEntity.count());
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2 + (target.type != payload.type ? 1 : 0),
				() -> this.assertPublish(this.registerComponentQueueName, payload));

		// check updated the components
		final var countComponentsAfter = this.assertItemNotNull(ComponentEntity.count());
		assertEquals(countComponentsBefore + 1, countComponentsAfter);
		// Get last component
		final ComponentEntity lastComponent = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastComponent.since);
		assertEquals(payload.name, lastComponent.name);
		assertEquals(payload.type, lastComponent.type);
		assertEquals(payload.version, lastComponent.version);
		assertEquals(apiVersion, lastComponent.apiVersion);
		assertNull(lastComponent.finishedTime);
		assertNotNull(lastComponent.channels);
		assertEquals(1, lastComponent.channels.size());
		assertEquals(sourceChannelName, lastComponent.channels.get(0).name);
		assertEquals(sourceChannelDescription, lastComponent.channels.get(0).description);
		assertNull(lastComponent.channels.get(0).subscribe);
		assertNotNull(lastComponent.channels.get(0).publish);
		assertInstanceOf(ObjectPayloadSchema.class, lastComponent.channels.get(0).publish);
		assertEquals(expectedSchema, lastComponent.channels.get(0).publish);

		// check updated the connections
		final var countConnectionsAfter = this.assertItemNotNull(TopologyConnectionEntity.count());
		assertEquals(countConnectionsBefore + 1, countConnectionsAfter);

		// Get last connection
		final TopologyConnectionEntity lastConnection = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastConnection.createTimestamp);
		assertTrue(lastConnection.createTimestamp <= lastConnection.updateTimestamp);
		assertNull(lastConnection.deletedTimestamp);
		assertNotNull(lastConnection.source);
		assertEquals(sourceChannelName, lastConnection.source.channelName);
		assertEquals(lastComponent.id, lastConnection.source.componentId);
		assertNotNull(lastConnection.target);
		assertEquals(targetChannel.name, lastConnection.target.channelName);
		assertEquals(target.id, lastConnection.target.componentId);

		// Check that the connection is enabled if the types are different
		assertEquals(target.type != payload.type, this.listener.isOpen(sourceChannelName));

	}

	/**
	 * Check that a component is created and is created a connection as the
	 * registered component as target.
	 */
	@Test
	public void shouldRegisterComponentAndCreateConnectionAsTarget() {

		// The message to register the source component of the connection
		final var componentTypeIndex = rnd().nextInt(0, 3);
		final var componentName = nextPattern("component_{0}");
		final var apiVersion = nextPattern("{0}.{1}.{2}", 3);
		final var apiDescription = nextPattern("Description of the API {0}");
		final var actionName = nextPattern("target_action_{0}");
		final var fieldName = actionName + nextPattern("_field_{0}");
		final var targetChannelName = MessageFormat.format("valawai/c{0}/{1}/data/{2}", componentTypeIndex,
				componentName, actionName);
		final var targetChannelDescription = nextPattern("Description of the target channel {0}");

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.name = MessageFormat.format("c{0}_{1}", componentTypeIndex, componentName);
		payload.type = ComponentType.values()[componentTypeIndex];
		payload.asyncapiYaml = MessageFormat.format(
				this.loadAsyncapiResourceTemplate("component_to_register_and_be_target.pattern.yml"),
				componentTypeIndex, componentName, apiVersion, apiDescription, actionName, targetChannelDescription,
				fieldName);

		// Create the component that will be the source of the connection
		ComponentEntity source = ComponentEntities.nextComponent();
		while (source.channels == null || source.channels.isEmpty()) {

			source = ComponentEntities.nextComponent();
		}
		ComponentEntities.minComponents(23);

		final var sourceChannel = source.channels.get(0);
		source.channels.clear();
		source.channels.add(sourceChannel);
		sourceChannel.subscribe = null;
		final var expectedSchema = new ObjectPayloadSchema();
		expectedSchema.properties.put(fieldName, BasicPayloadSchema.with(BasicPayloadFormat.STRING));
		sourceChannel.publish = expectedSchema;
		this.assertItemNotNull(source.update());

		final var countConnectionsBefore = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var countComponentsBefore = this.assertItemNotNull(ComponentEntity.count());
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2 + (source.type != payload.type ? 1 : 0),
				() -> this.assertPublish(this.registerComponentQueueName, payload));

		// check updated the components
		final var countComponentsAfter = this.assertItemNotNull(ComponentEntity.count());
		assertEquals(countComponentsBefore + 1, countComponentsAfter);
		// Get last component
		final ComponentEntity lastComponent = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastComponent.since);
		assertEquals(payload.name, lastComponent.name);
		assertEquals(payload.type, lastComponent.type);
		assertEquals(payload.version, lastComponent.version);
		assertEquals(apiVersion, lastComponent.apiVersion);
		assertNull(lastComponent.finishedTime);
		assertNotNull(lastComponent.channels);
		assertEquals(1, lastComponent.channels.size());
		assertEquals(targetChannelName, lastComponent.channels.get(0).name);
		assertEquals(targetChannelDescription, lastComponent.channels.get(0).description);
		assertNull(lastComponent.channels.get(0).publish);
		assertNotNull(lastComponent.channels.get(0).subscribe);
		assertInstanceOf(ObjectPayloadSchema.class, lastComponent.channels.get(0).subscribe);
		assertEquals(expectedSchema, lastComponent.channels.get(0).subscribe);

		// check updated the connections
		final var countConnectionsAfter = this.assertItemNotNull(TopologyConnectionEntity.count());
		assertEquals(countConnectionsBefore + 1, countConnectionsAfter);

		// Get last connection
		final TopologyConnectionEntity lastConnection = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastConnection.createTimestamp);
		assertTrue(lastConnection.createTimestamp <= lastConnection.updateTimestamp);
		assertNull(lastConnection.deletedTimestamp);
		assertNotNull(lastConnection.target);
		assertEquals(targetChannelName, lastConnection.target.channelName);
		assertEquals(lastComponent.id, lastConnection.target.componentId);
		assertNotNull(lastConnection.source);
		assertEquals(sourceChannel.name, lastConnection.source.channelName);
		assertEquals(source.id, lastConnection.source.componentId);

		// Check that the connection is enabled if the types are different
		assertEquals(source.type != payload.type, this.listener.isOpen(sourceChannel.name));

	}

	/**
	 * Check that a component is registered and it is notified when is done.
	 */
	@Test
	public void shouldRegisterComponentAndNotifyWhenIsDone() {

		// The message to register the target component of the connection
		final var componentTypeIndex = rnd().nextInt(0, 3);
		final var componentName = nextPattern("component_{0}");
		final var outFieldName = nextPattern("field_to_test_{0}");
		final var inFieldName = nextPattern("field_to_test_notification_{0}");

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.type = ComponentType.values()[componentTypeIndex];
		payload.asyncapiYaml = MessageFormat.format(
				this.loadAsyncapiResourceTemplate("component_to_register_and_notity.pattern.yml"), componentTypeIndex,
				componentName, outFieldName, inFieldName);

		// Create the component that will be the source of the connection
		final var sourceNext = new ComponentTest().nextModel();
		final ComponentEntity source = new ComponentEntity();
		source.apiVersion = sourceNext.apiVersion;
		source.channels = new ArrayList<ChannelSchema>();
		source.description = sourceNext.description;
		source.name = sourceNext.name;
		source.since = sourceNext.since;
		source.type = sourceNext.type;
		while (payload.type == source.type) {

			source.type = next(ComponentType.values());
		}
		source.version = sourceNext.version;
		final var sourceChannel = new ChannelSchema();
		final var sourceChannelName = nextPattern("test/publish_{0}");

		sourceChannel.name = sourceChannelName;
		final var sourceObject = new ObjectPayloadSchema();
		final var basic = new BasicPayloadSchema();
		basic.format = BasicPayloadFormat.STRING;
		sourceObject.properties.put(inFieldName, basic);
		sourceChannel.publish = sourceObject;
		source.channels.add(sourceChannel);
		this.assertItemNotNull(source.persist());

		// Create the component that will be the target of the connection
		final var targetNext = new ComponentTest().nextModel();
		final ComponentEntity target = new ComponentEntity();
		target.apiVersion = targetNext.apiVersion;
		target.channels = new ArrayList<ChannelSchema>();
		target.description = targetNext.description;
		target.name = targetNext.name;
		target.since = targetNext.since;
		target.type = targetNext.type;
		while (payload.type == target.type) {

			target.type = next(ComponentType.values());
		}
		target.version = targetNext.version;
		final var targetChannel = new ChannelSchema();
		final var targetChannelName = nextPattern("test/subscribe_{0}");

		targetChannel.name = targetChannelName;
		final var targetObject = new ObjectPayloadSchema();
		targetObject.properties.put(outFieldName, basic);
		targetChannel.subscribe = targetObject;
		target.channels.add(targetChannel);
		this.assertItemNotNull(target.persist());

		final var countConnectionsBefore = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var countComponentsBefore = this.assertItemNotNull(ComponentEntity.count());
		final var queueName = MessageFormat.format("valawai/c{0}/{1}/control/registered", componentTypeIndex,
				componentName);
		final var queue = this.waitOpenQueue(queueName);

		// register the component
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(3, () -> this.assertPublish(this.registerComponentQueueName, payload));

		// wait notify the component is registered
		final var registered = queue.waitReceiveMessage(ComponentPayload.class);
		assertEquals(payload.name, registered.name);
		final var expectedComponent = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
		assertEquals(expectedComponent.description, registered.description);
		assertEquals(payload.version, registered.version);
		assertEquals(expectedComponent.apiVersion, registered.apiVersion);
		assertEquals(payload.type, registered.type);
		assertTrue(now <= registered.since);
		assertEquals(expectedComponent.channels, registered.channels);
		final var expectedTargetChannelName = MessageFormat.format("valawai/c{0}/{1}/data/input", componentTypeIndex,
				componentName);
		final var expectedSourceChannelName = MessageFormat.format("valawai/c{0}/{1}/data/output", componentTypeIndex,
				componentName);

		// check updated the components
		final var countComponentsAfter = this.assertItemNotNull(ComponentEntity.count());
		assertEquals(countComponentsBefore + 1, countComponentsAfter);
		// Get last component
		final ComponentEntity lastComponent = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.descending("_id")).firstResult());
		assertEquals(registered.id, lastComponent.id);
		assertTrue(now <= lastComponent.since);
		assertEquals(payload.name, lastComponent.name);
		assertEquals(payload.type, lastComponent.type);
		assertEquals(payload.version, lastComponent.version);
		assertEquals(expectedComponent.apiVersion, lastComponent.apiVersion);
		assertEquals(expectedComponent.channels, lastComponent.channels);

		// check updated the connections
		final var countConnectionsAfter = this.assertItemNotNull(TopologyConnectionEntity.count());
		assertEquals(countConnectionsBefore + 2, countConnectionsAfter);

		// Get last connection
		final TopologyConnectionEntity lastConnectionWithComponentAsTarget = this
				.assertItemNotNull(TopologyConnectionEntity
						.find("target.componentId = ?1", Sort.descending("_id"), lastComponent.id).firstResult());
		assertTrue(now <= lastConnectionWithComponentAsTarget.createTimestamp);
		assertTrue(
				lastConnectionWithComponentAsTarget.createTimestamp <= lastConnectionWithComponentAsTarget.updateTimestamp);
		assertNull(lastConnectionWithComponentAsTarget.deletedTimestamp);
		assertNotNull(lastConnectionWithComponentAsTarget.target);
		assertEquals(expectedTargetChannelName, lastConnectionWithComponentAsTarget.target.channelName);
		assertEquals(lastComponent.id, lastConnectionWithComponentAsTarget.target.componentId);
		assertNotNull(lastConnectionWithComponentAsTarget.source);
		assertEquals(sourceChannelName, lastConnectionWithComponentAsTarget.source.channelName);
		assertEquals(source.id, lastConnectionWithComponentAsTarget.source.componentId);

		// Check that the connection is working
		assertTrue(this.listener.isOpen(lastConnectionWithComponentAsTarget.source.channelName));

		// Get last connection
		final TopologyConnectionEntity lastConnectionWithComponentAsSource = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastConnectionWithComponentAsSource.createTimestamp);
		assertTrue(
				lastConnectionWithComponentAsSource.createTimestamp <= lastConnectionWithComponentAsSource.updateTimestamp);
		assertNull(lastConnectionWithComponentAsSource.deletedTimestamp);
		assertNotNull(lastConnectionWithComponentAsSource.source);
		assertEquals(expectedSourceChannelName, lastConnectionWithComponentAsSource.source.channelName);
		assertEquals(lastComponent.id, lastConnectionWithComponentAsSource.source.componentId);
		assertNotNull(lastConnectionWithComponentAsSource.target);
		assertEquals(targetChannelName, lastConnectionWithComponentAsSource.target.channelName);
		assertEquals(target.id, lastConnectionWithComponentAsSource.target.componentId);

		// Check that the connection is working
		assertTrue(this.listener.isOpen(lastConnectionWithComponentAsSource.source.channelName));

	}

	/**
	 * Check that is load an specification resource.
	 *
	 * @param name of the resource to load.
	 *
	 * @return the string of the loaded resource template.
	 */
	private String loadAsyncapiResourceTemplate(String name) {

		try {

			final var loader = RegisterComponentPayloadTest.class.getClassLoader();
			final var stream = loader.getResourceAsStream("eu/valawai/mov/events/components/" + name);
			final var bytes = stream.readAllBytes();
			return new String(bytes, StandardCharsets.UTF_8);

		} catch (final Throwable error) {

			fail(error.getMessage());
			return null;
		}

	}

	/**
	 * Check that a component is registered and is subscribed to come connections
	 * that it may be notified.
	 */
	@Test
	public void shouldRegisterComponentAndSubscribedToConnecions() {

		// The message to register the target component of the connection
		final var componentName = nextPattern("component_{0}");
		final var actionName = nextPattern("action_{0}");
		final var payload = new RegisterComponentPayloadTest().nextModel();
		final var fieldName = nextPattern("subscribe_into_connection_field_to_test_{0}");
		payload.type = ComponentType.C2;
		payload.asyncapiYaml = MessageFormat.format(
				this.loadAsyncapiResourceTemplate("component_to_register_and_subscribe.pattern.yml"), componentName,
				actionName, fieldName);

		// create the connections where the component must be subscribed
		final var connectionSchema = new ObjectPayloadSchema();
		connectionSchema.properties.put(fieldName, BasicPayloadSchema.with(BasicPayloadFormat.STRING));
		final List<TopologyConnectionEntity> connections = new ArrayList<>();
		for (var i = 0; i < 1; i++) {

			final var connection = TopologyConnectionEntities.nextTopologyConnection();
			connections.add(connection);
			final ComponentEntity source = this
					.assertItemNotNull(ComponentEntity.findById(connection.source.componentId));
			for (final var channel : source.channels) {

				if (channel.name.equals(connection.source.channelName)) {

					channel.publish = connectionSchema;
					this.assertItemNotNull(source.update());
					break;
				}
			}
			final ComponentEntity target = this
					.assertItemNotNull(ComponentEntity.findById(connection.target.componentId));
			for (final var channel : target.channels) {

				if (channel.name.equals(connection.target.channelName)) {

					channel.subscribe = connectionSchema;
					this.assertItemNotNull(target.update());
					break;
				}
			}

		}

		final var countComponentsBefore = this.assertItemNotNull(ComponentEntity.count());
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(1 + connections.size(),
				() -> this.assertPublish(this.registerComponentQueueName, payload));

		// check updated the components
		final var countComponentsAfter = this.assertItemNotNull(ComponentEntity.count());
		assertEquals(countComponentsBefore + 1, countComponentsAfter);
		// Get last component
		final ComponentEntity lastComponent = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastComponent.since);
		assertEquals(payload.name, lastComponent.name);
		assertEquals(payload.type, lastComponent.type);
		assertEquals(payload.version, lastComponent.version);
		assertEquals("1.0.0", lastComponent.apiVersion);
		assertNull(lastComponent.finishedTime);
		assertNotNull(lastComponent.channels);
		assertEquals(3, lastComponent.channels.size());

		// check updated the connections
		final var expectedSubscriptionNode = new TopologyNode();
		expectedSubscriptionNode.componentId = lastComponent.id;
		expectedSubscriptionNode.channelName = "valawai/c2/" + componentName + "/control/" + actionName;
		for (final var connection : connections) {

			final TopologyConnectionEntity updated = this
					.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
			assertNotNull(updated.c2Subscriptions, "New component is not subscribed into the connection");
			assertTrue(updated.c2Subscriptions.contains(expectedSubscriptionNode),
					"New component is not subscribed into the connection");
			assertTrue(now <= updated.updateTimestamp, "The connection is not updated");
		}
	}

	/**
	 * Check that a component is registered and it is notified when is done.
	 */
	@Test
	public void shouldRegisterComponentAndNotifyWhenIsDone2() {

		// The message to register the target component of the connection
		final var componentTypeIndex = rnd().nextInt(0, 3);
		final var componentName = nextPattern("component_{0}");
		final var outFieldName = nextPattern("field_to_test_{0}");
		final var inFieldName = nextPattern("field_to_test_notification_{0}");

		final var payload = new RegisterComponentPayloadTest().nextModel();
		payload.type = ComponentType.values()[componentTypeIndex];
		payload.asyncapiYaml = MessageFormat.format(
				this.loadAsyncapiResourceTemplate("component_to_register_and_notity.pattern.yml"), componentTypeIndex,
				componentName, outFieldName, inFieldName);

		final var queueName = MessageFormat.format("valawai/c{0}/{1}/control/registered", componentTypeIndex,
				componentName);
		final var queue = this.waitOpenQueue(queueName);

		// register the component
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerComponentQueueName, payload));

		// wait notify the component is registered
		final var registered = queue.waitReceiveMessage(ComponentPayload.class);
		assertEquals(payload.name, registered.name);
		final var expectedComponent = ComponentBuilder.fromAsyncapi(payload.asyncapiYaml);
		assertEquals(expectedComponent.description, registered.description);
		assertEquals(payload.version, registered.version);
		assertEquals(expectedComponent.apiVersion, registered.apiVersion);
		assertEquals(payload.type, registered.type);
		assertTrue(now <= registered.since);
		assertEquals(expectedComponent.channels, registered.channels);

	}

}
