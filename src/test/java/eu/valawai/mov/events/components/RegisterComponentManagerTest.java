/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.nextPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.api.v1.components.BasicPayloadFormat;
import eu.valawai.mov.api.v1.components.BasicPayloadSchema;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentTest;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.logs.LogEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
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
	 * The URL of the application.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.register_component.queue.name", defaultValue = "valawai/component/register")
	String registerCcomponentQueueName;

	/**
	 * Check that cannot register with an invalid payload.
	 */
	@Test
	public void shouldNotRegisterComponentWithInvalidPayload() {

		final var payload = new RegisterComponentPayload();
		final var countComponents = ComponentEntity.count().await().atMost(Duration.ofSeconds(30));

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

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

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

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

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
		assertEquals(countComponents, ComponentEntity.count().await().atMost(Duration.ofSeconds(30)));

	}

	/**
	 * Check that the user register a component.
	 */
	@Test
	public void shouldRegisterComponent() {

		final var payload = new RegisterComponentPayloadTest().nextModel();
		final var apiVersion = nextPattern("{0}.{1}.{2}", 3);
		final var channelName = nextPattern("test/register_component_{0}");
		final var fieldName = nextPattern("field_to_test_{0}");
		payload.asyncapiYaml = String.format("""
				asyncapi: 2.6.0
				info:
				  title: Test register component
				  version: {0}
				  description: Used to test the register component
				channels:
				  {1}:
				    publish:
				      message:
				        payload:
				          type: object
				          properties:
				            {2}:
				              type: string
				""", apiVersion, channelName, fieldName).trim().replaceAll("\\t", "");

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
		channel.id = channelName;
		final var object = new ObjectPayloadSchema();
		final var basic = new BasicPayloadSchema();
		basic.format = BasicPayloadFormat.STRING;
		object.properties.put(fieldName, basic);
		channel.subscribe = object;
		target.channels.add(channel);

		this.assertItemNotNull(target.persist());

		final var countConnections = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var countComponents = this.assertItemNotNull(ComponentEntity.count());
		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.registerCcomponentQueueName, payload));

	}

}
