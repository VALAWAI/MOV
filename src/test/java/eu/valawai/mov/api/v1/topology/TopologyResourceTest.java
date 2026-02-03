/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.rnd;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.ListenerService;
import eu.valawai.mov.events.topology.TopologyAction;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link TopologyResource}.
 *
 * @see TopologyResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class TopologyResourceTest extends APITestCase {

	/**
	 * The service to listen for events.
	 */
	@Inject
	protected ListenerService listener;

	/**
	 * Create some connections that can be used.
	 */
	@BeforeAll
	public static void createConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
	}

	/**
	 * Should not get a page with a bad order.
	 */
	@Test
	public void shouldNotGetPageWithBadOrder() {

		given().when().queryParam("order", "undefined").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad offset.
	 */
	@Test
	public void shouldNotGetPageWithBadOffset() {

		given().when().queryParam("offset", "-1").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not get a page with a bad limit.
	 */
	@Test
	public void shouldNotGetPageWithBadLimit() {

		given().when().queryParam("limit", "0").get("/v1/topology/connections").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should get empty page.
	 */
	@Test
	public void shouldGetEmptyPage() {

		final var page = given().when().queryParam("pattern", "1").queryParam("limit", "1").queryParam("offset", "3")
				.get("/v1/topology/connections").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(MinConnectionPage.class);
		final var expected = new MinConnectionPage();
		expected.offset = 3;
		assertEquals(expected, page);
	}

	/**
	 * Should get page with pattern.
	 */
	@Test
	public void shouldGetPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new MinConnectionPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.regex("source.channelName", pattern), Filters.regex("target.channelName", pattern)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l1.target.channelName.compareTo(l2.target.channelName);
			if (cmp == 0) {

				cmp = l2.source.channelName.compareTo(l1.source.channelName);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = given().when().queryParam("pattern", "/" + pattern + "/")
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "target,-source").get("/v1/topology/connections").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(MinConnectionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should get page for a component.
	 */
	@Test
	public void shouldGetPageWithComponentId() {

		final var expected = new MinConnectionPage();
		expected.offset = rnd().nextInt(2, 5);

		final var limit = rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;

		final var component = ComponentEntities.nextComponent();
		for (var i = 0; i < max; i++) {

			final var connection = TopologyConnectionEntities.nextTopologyConnection();
			if (i % 2 == 0) {

				connection.source.componentId = component.id;

			} else {

				connection.target.componentId = component.id;
			}
			this.assertItemNotNull(connection.update());
			if (connection.deletedTimestamp != null) {

				i--;
			}
		}

		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.eq("source.componentId", component.id),
						Filters.eq("target.componentId", component.id)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l1.target.channelName.compareTo(l2.target.channelName);
			if (cmp == 0) {

				cmp = l2.source.channelName.compareTo(l1.source.channelName);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = given().when().queryParam("component", component.id.toHexString())
				.queryParam("limit", String.valueOf(limit)).queryParam("offset", String.valueOf(expected.offset))
				.queryParam("order", "target,-source").get("/v1/topology/connections").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(MinConnectionPage.class);
		assertEquals(expected, page);

	}

	/**
	 * Should not get an undefined connection.
	 */
	@Test
	public void shouldNotFoundUndefinedConnection() {

		final var id = nextObjectId().toHexString();
		given().when().get("/v1/topology/connections/" + id).then().statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Should get a connection.
	 */
	@Test
	public void shouldGetConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();
		final var expected = TopologyConnectionTest.from(connection);
		final var id = connection.id.toHexString();
		final var result = given().when().get("/v1/topology/connections/" + id).then()
				.statusCode(Status.OK.getStatusCode()).extract().as(TopologyConnection.class);
		assertEquals(expected, result);

	}

	/**
	 * Should create a connection and enable it.
	 */
	@Test
	public void shouldCreateConnectionAndEnableConnection() {

		final var create = this.createValidConnectionToCreate();
		create.enabled = true;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLogs(2, () -> given().contentType(ContentType.JSON).body(create).when()
				.post("/v1/topology/connections").then().statusCode(Status.NO_CONTENT.getStatusCode()));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.findAll(Sort.descending("_id")).firstResult());
		assertTrue(now <= last.createTimestamp);
		assertTrue(last.createTimestamp <= last.updateTimestamp);
		assertNull(last.deletedTimestamp);
		assertNotNull(last.source);
		assertEquals(create.sourceComponent, last.source.componentId);
		assertEquals(create.sourceChannel, last.source.channelName);
		assertNotNull(last.target);
		assertEquals(create.targetComponent, last.target.componentId);
		assertEquals(create.targetChannel, last.target.channelName);
		assertTrue(last.enabled);

		assertTrue(this.listener.isOpen(create.sourceChannel));

		assertEquals(2l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));
		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".+" + create.sourceChannel + ".+" + create.targetChannel + ".+", now)));
	}

	/**
	 * Create a valid {2link ConnectionToCreate}.
	 *
	 * @return the valid connection to create.
	 */
	private ConnectionToCreate createValidConnectionToCreate() {

		final var create = new ConnectionToCreate();
		PayloadSchema schema = null;
		do {

			final var component = ComponentEntities.nextComponent();
			if (component.channels != null) {

				for (final var channel : component.channels) {

					if (create.sourceComponent == null && channel.publish != null && channel.subscribe == null) {

						create.sourceComponent = component.id;
						create.sourceChannel = channel.name;
						if (schema != null) {

							channel.publish = schema;
							this.assertItemNotNull(component.update());

						} else {

							schema = channel.publish;
						}
						break;

					} else if (create.targetComponent == null && channel.publish == null && channel.subscribe != null) {

						create.targetComponent = component.id;
						create.targetChannel = channel.name;
						if (schema != null) {

							channel.subscribe = schema;
							this.assertItemNotNull(component.update());

						} else {

							schema = channel.subscribe;
						}
						break;

					}
				}
			}

		} while (create.sourceComponent == null || create.targetComponent == null);
		return create;
	}

	/**
	 * Should create a connection without enable it.
	 */
	@Test
	public void shouldCreateConnectionAndNotEnableConnection() {

		final var create = this.createValidConnectionToCreate();
		create.enabled = false;

		final var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> given().contentType(ContentType.JSON).body(create).when()
				.post("/v1/topology/connections").then().statusCode(Status.NO_CONTENT.getStatusCode()));

		final TopologyConnectionEntity last = this
				.assertItemNotNull(TopologyConnectionEntity.find("source.componentId = ?1 and target.componentId = ?2",
						Sort.descending("_id"), create.sourceComponent, create.targetComponent).firstResult());

		assertEquals(1l, this.assertItemNotNull(LogEntity.count("level = ?1 and message like ?2 and timestamp >= ?3",
				LogLevel.INFO, ".*" + last.id.toHexString() + ".*", now)));

		assertTrue(now <= last.createTimestamp);
		assertEquals(last.updateTimestamp, last.createTimestamp);
		assertNull(last.deletedTimestamp);
		assertNotNull(last.source);
		assertEquals(create.sourceComponent, last.source.componentId);
		assertEquals(create.sourceChannel, last.source.channelName);
		assertNotNull(last.target);
		assertEquals(create.targetComponent, last.target.componentId);
		assertEquals(create.targetChannel, last.target.channelName);
		assertFalse(last.enabled);

		assertFalse(this.listener.isOpen(create.sourceChannel));

	}

	/**
	 * Should enable and disable a connection.
	 */
	@Test
	public void shouldEnableAndDisable() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var change = new ChangeConnection();
		change.action = TopologyAction.ENABLE;
		change.connectionId = connection.id;
		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> given().contentType(ContentType.JSON).body(change).when()
				.put("/v1/topology/connections/change").then().statusCode(Status.NO_CONTENT.getStatusCode()));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		change.action = TopologyAction.DISABLE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> given().contentType(ContentType.JSON).body(change).when()
				.put("/v1/topology/connections/change").then().statusCode(Status.NO_CONTENT.getStatusCode()));
		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Disabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertFalse(current.enabled);
		assertFalse(this.listener.isOpen(connection.source.channelName));

	}

	/**
	 * Should enable and remove a connection.
	 */
	@Test
	public void shouldEnableAndRemove() {

		var connection = TopologyConnectionEntities.nextTopologyConnection();
		while (connection.enabled) {

			connection = TopologyConnectionEntities.nextTopologyConnection();
		}

		final var change = new ChangeConnection();
		change.action = TopologyAction.ENABLE;
		change.connectionId = connection.id;
		var now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> given().contentType(ContentType.JSON).body(change).when()
				.put("/v1/topology/connections/change").then().statusCode(Status.NO_CONTENT.getStatusCode()));

		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.INFO,
								"Enabled .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		TopologyConnectionEntity current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertTrue(now <= current.updateTimestamp);
		assertTrue(current.enabled);
		assertTrue(this.listener.isOpen(connection.source.channelName));

		change.action = TopologyAction.REMOVE;
		now = TimeManager.now();
		this.executeAndWaitUntilNewLog(() -> given().contentType(ContentType.JSON).body(change).when()
				.put("/v1/topology/connections/change").then().statusCode(Status.NO_CONTENT.getStatusCode()));

		assertEquals(1l,
				LogEntity
						.count("level = ?1 and message like ?2", LogLevel.DEBUG,
								"Removed .*" + connection.id.toHexString() + ".*")
						.await().atMost(Duration.ofSeconds(30)));

		current = this.assertItemNotNull(TopologyConnectionEntity.findById(connection.id));
		assertNotNull(current.deletedTimestamp);
		assertTrue(now <= current.deletedTimestamp);
		assertFalse(this.listener.isOpen(connection.source.channelName));
	}

}
