/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.BasicPayloadFormat;
import eu.valawai.mov.api.v1.components.BasicPayloadSchema;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentTest;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
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

		// Guarantee that exist some components
		ComponentEntities.minComponents(100);

		// The message to register the source component of the connection
		final var payload = new RegisterComponentPayloadTest().nextModel();
		final var apiVersion = nextPattern("{0}.{1}.{2}", 3);
		final var sourceChannelName = nextPattern("test/register_component_publish_{0}");
		final var sourceChannelDescription = nextPattern("Description of a channel {0}");
		final var fieldName = nextPattern("field_to_test_{0}");
		payload.asyncapiYaml = MessageFormat.format("""
				asyncapi: 2.6.0
				info:
				  title: Test description of a publishing
				  version: {0}
				  description: API description
				channels:
				  {1}:
				    description: {2}
				    publish:
				      message:
				        payload:
				          type: object
				          properties:
				            {3}:
				              type: string
								""", apiVersion, sourceChannelName, sourceChannelDescription, fieldName).trim()
				.replaceAll("\\t", "");

		// Create the component that will be the target of the connection
		final var next = new ComponentTest().nextModel();
		final ComponentEntity target = new ComponentEntity();
		target.apiVersion = next.apiVersion;
		target.channels = new ArrayList<ChannelSchema>();
		target.description = next.description;
		target.name = next.name;
		target.since = next.since;
		target.type = next.type;
		target.version = next.version;
		final var channel = new ChannelSchema();
		final var targetChannelName = nextPattern("test/register_component_subscribe_{0}");
		;
		channel.id = targetChannelName;
		final var object = new ObjectPayloadSchema();
		final var basic = new BasicPayloadSchema();
		basic.format = BasicPayloadFormat.STRING;
		object.properties.put(fieldName, basic);
		channel.subscribe = object;
		target.channels.add(channel);

		this.assertItemNotNull(target.persist());

		final var countConnectionsBefore = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var countComponentsBefore = this.assertItemNotNull(ComponentEntity.count());
		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(3, () -> this.assertPublish(this.registerComponentQueueName, payload));

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
		assertEquals(sourceChannelName, lastComponent.channels.get(0).id);
		assertEquals(sourceChannelDescription, lastComponent.channels.get(0).description);
		assertNull(lastComponent.channels.get(0).subscribe);
		assertNotNull(lastComponent.channels.get(0).publish);
		assertInstanceOf(ObjectPayloadSchema.class, lastComponent.channels.get(0).publish);
		assertEquals(object, lastComponent.channels.get(0).publish);

		// check updated the connections
		final var countConnectionsAfter = this.assertItemNotNull(TopologyConnectionEntity.count());
		assertEquals(countConnectionsBefore + 1, countConnectionsAfter);

		// Get last connection
		final TopologyConnectionEntity lastConnection = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= lastConnection.createTimestamp);
		assertEquals(lastConnection.createTimestamp, lastConnection.updateTimestamp);
		assertNull(lastConnection.deletedTimestamp);
		assertNotNull(lastConnection.source);
		assertEquals(sourceChannelName, lastConnection.source.channelName);
		assertEquals(lastComponent.id, lastConnection.source.componentId);
		assertNotNull(lastConnection.target);
		assertEquals(targetChannelName, lastConnection.target.channelName);
		assertEquals(target.id, lastConnection.target.componentId);

		// Check that the connection is working
		assertTrue(this.listener.isOpen(sourceChannelName));

	}

	/**
	 * Check that a component is created and is created a connection as the
	 * registered component as target.
	 */
	@Test
	public void shouldRegisterComponentAndCreateConnectionAsTarget() {

		// Guarantee that exist some components
		ComponentEntities.minComponents(100);
	}

}
