/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link QueryConnectionsManager}.
 *
 * @see QueryConnectionsManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class QueryConnectionsManagerTest extends MovEventTestCase {

	/**
	 * The name of the queue to send the query to obtain some connections.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.query_connections.queue.name", defaultValue = "valawai/connection/query")
	String queryConnectionstQueueName;

	/**
	 * The name of the queue to send the query to obtain some connections.
	 */
	@ConfigProperty(name = "mp.messaging.outgoing.connections_page.queue.name", defaultValue = "valawai/connection/page")
	String connectionsPagetQueueName;

	/**
	 * Check that cannot ask for connections with an invalid payload.
	 */
	@Test
	public void shouldNotQueryConnectionsWithInvalidPayload() {

		final var payload = new QueryConnectionsPayload();
		payload.offset = -1;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryConnectionstQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and payload = ?2", LogLevel.ERROR, Json.encodePrettily(payload))
				.await().atMost(Duration.ofSeconds(30)));
	}

	/**
	 * Check that query return an empty page.
	 */
	@Test
	public void shouldQueryReturnrEmptyPage() {

		final var queue = this.waitOpenQueue(this.connectionsPagetQueueName);

		final var payload = new QueryConnectionsPayload();
		payload.id = nextUUID().toString();
		payload.offset = 3;
		payload.limit = 7;
		payload.sourceComponentId = nextUUID().toString();

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryConnectionstQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + payload.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));
		final var msg = queue.waitReceiveMessage(ConnectionsPagePayload.class);
		assertEquals(payload.id, msg.queryId);
		assertEquals(0l, msg.total);
		assertNull(msg.connections);

	}

	/**
	 * Check that query with specific source and target channel name.
	 */
	@Test
	public void shouldQueryWithSourceAndTargetChannelName() {

		final var queue = this.waitOpenQueue(this.connectionsPagetQueueName);

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var channelSourceNamePattern = ".+c1.+";
		query.sourceChannelName = "/" + channelSourceNamePattern + "/";
		final var channelTargetNamePattern = ".+c[0|2].+";
		query.targetChannelName = "/" + channelTargetNamePattern + "/";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.and(Filters.regex("source.channelName", channelSourceNamePattern),
				Filters.regex("target.channelName", channelTargetNamePattern));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = Long.compare(l2.updateTimestamp, l1.updateTimestamp);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = ConnectionPayloadTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryConnectionstQueueName, query));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + query.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final var page = queue.waitReceiveMessage(ConnectionsPagePayload.class);
		assertEquals(expected, page);

	}

	/**
	 * Check that query with specific source channel name.
	 */
	@Test
	public void shouldQueryWithSourceChannelName() {

		final var queue = this.waitOpenQueue(this.connectionsPagetQueueName);

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var channelSourceNamePattern = ".+c1.+";
		query.sourceChannelName = "/" + channelSourceNamePattern + "/";
		query.order = "-createTimestamp";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.regex("source.channelName", channelSourceNamePattern);
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = Long.compare(l2.createTimestamp, l1.createTimestamp);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = ConnectionPayloadTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryConnectionstQueueName, query));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + query.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final var page = queue.waitReceiveMessage(ConnectionsPagePayload.class);
		assertEquals(expected, page);

	}

	/**
	 * Check that query with specific target channel name.
	 */
	@Test
	public void shouldQueryWithTargetChannelName() {

		final var queue = this.waitOpenQueue(this.connectionsPagetQueueName);

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var channelTargetNamePattern = ".+c1.+";
		query.targetChannelName = "/" + channelTargetNamePattern + "/";
		query.order = "-enabled";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.regex("target.channelName", channelTargetNamePattern);
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = Boolean.compare(l2.enabled, l1.enabled);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.connections = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = ConnectionPayloadTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryConnectionstQueueName, query));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + query.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final var page = queue.waitReceiveMessage(ConnectionsPagePayload.class);
		assertEquals(expected, page);

	}

}
