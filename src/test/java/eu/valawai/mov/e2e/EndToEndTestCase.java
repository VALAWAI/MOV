/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.e2e;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.MinComponentPage;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.events.components.ComponentPayload;
import eu.valawai.mov.events.components.RegisterComponentPayload;
import eu.valawai.mov.events.components.RegisterComponentPayloadTest;
import eu.valawai.mov.events.components.UnregisterComponentPayload;
import eu.valawai.mov.persistence.components.ComponentEntity;
import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import jakarta.ws.rs.core.Response.Status;

/**
 * Generic class that represents an end-to-end test.
 *
 * @author VALAWAI
 */
public class EndToEndTestCase extends MovEventTestCase {

	/**
	 * Register a component.
	 *
	 * @param name of the resource that contains the component to register. It must
	 *             math the pattern C[0|1|2]_(\w+)_(\d\.\d\.\d), thus type, name and
	 *             version.
	 *
	 * @return the payload to register the component with the specified name.
	 */
	protected RegisterComponentPayload createRegisterComponentPayloadForResource(String name) {

		final var payload = new RegisterComponentPayload();
		payload.type = ComponentType.valueOf(name.substring(0, 2).toUpperCase());
		final var index = name.lastIndexOf("_");
		payload.name = name.substring(0, 3).toLowerCase() + name.substring(3, index);
		payload.version = name.substring(index + 1);
		try {

			final var loader = RegisterComponentPayloadTest.class.getClassLoader();
			final var stream = loader.getResourceAsStream("eu/valawai/mov/e2e/" + name + ".yml");
			final var bytes = stream.readAllBytes();
			payload.asyncapiYaml = new String(bytes, StandardCharsets.UTF_8);

		} catch (final Throwable error) {

			fail(error.getMessage());
		}

		return payload;

	}

	/**
	 * Register a component.
	 *
	 * @param payload with the information of the component to register.
	 *
	 * @return the component that has been registered.
	 */
	protected ComponentSimulator assertRegister(RegisterComponentPayload payload) {

		// create queue to receive registered information
		final var registeredQueueName = "valawai/" + payload.type.name().toLowerCase() + "/" + payload.name.substring(3)
				+ "/control/registered";
		final ComponentSimulator component = new ComponentSimulator();
		final var queue = this.waitOpenQueue(registeredQueueName);

		// Send register petition
		this.executeAndWaitUntilNewLog(() -> this.assertPublish("valawai/component/register", payload));

		// wait until the component is registered
		final var registered = queue.waitReceiveMessage(ComponentPayload.class);
		component.id = registered.id;
		assertEquals(payload.name, registered.name);
		assertEquals(payload.type, registered.type);
		assertEquals(payload.version, registered.version);

		// close the registered queue
		this.assertItemIsNull(this.listener.close(registeredQueueName));

		// check that the component has been registered in the data base
		final var page = given().when().queryParam("pattern", "/^" + registered.name + "$/")
				.queryParam("type", registered.type.name()).queryParam("limit", "100").queryParam("offset", "0")
				.get("/v1/components").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinComponentPage.class);
		assertTrue(page.total > 0);
		assertNotNull(page.components);
		var found = false;
		for (final var defined : page.components) {

			if (defined.id.equals(component.id)) {

				assertEquals(payload.name, defined.name);
				assertEquals(payload.type, defined.type);
				found = true;
				break;
			}

		}
		assertTrue(found, "Not found registered component");

		final var defined = given().when().get("/v1/components/" + component.id.toHexString()).then()
				.statusCode(Status.OK.getStatusCode()).extract().as(Component.class);
		assertEquals(registered.type, defined.type);
		assertEquals(registered.name, defined.name);
		assertEquals(registered.description, defined.description);
		assertEquals(registered.version, defined.version);
		assertEquals(registered.apiVersion, defined.apiVersion);
		assertEquals(registered.since, defined.since);
		assertEquals(registered.channels, defined.channels);

		for (final var channel : registered.channels) {

			if (channel.subscribe != null && !channel.name.equals(registeredQueueName)) {

				final var channelQueue = this.waitOpenQueue(channel.name);
				component.queues.put(channel.name, channelQueue);
			}
		}
		return component;
	}

	/**
	 * Unregister a component.
	 *
	 * @param component to unregister.
	 */
	protected void assertUnregister(ComponentSimulator component) {

		final var payload = new UnregisterComponentPayload();
		payload.componentId = component.id;
		this.executeAndWaitUntilNewLog(() -> this.assertPublish("valawai/component/unregister", payload));

		this.waitUntilNotNull(
				() -> ComponentEntity
						.count(Filters.and(Filters.eq("_id", component.id), Filters.ne("finishedTime", null))),
				count -> count == 1);

		this.waitUntilNotNull(
				() -> TopologyConnectionEntity.count(Filters.and(
						Filters.or(Filters.eq("source.componentId", payload.componentId),
								Filters.eq("target.componentId", payload.componentId)),
						Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)))),
				count -> count == 0);

		for (final var queueName : component.queues.keySet()) {

			if (this.listener.isOpen(queueName)) {

				this.assertItemIsNull(this.listener.close(queueName));
			}
		}
	}
}
