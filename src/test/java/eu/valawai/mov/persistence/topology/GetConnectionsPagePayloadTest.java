/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.flipCoin;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;
import static eu.valawai.mov.persistence.topology.TopologyConnectionEntities.nextTopologyConnections;
import static eu.valawai.mov.persistence.topology.TopologyConnectionEntities.nextTopologyConnectionsUntil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.events.topology.ConnectionPayloadTest;
import eu.valawai.mov.events.topology.ConnectionsPagePayload;
import eu.valawai.mov.events.topology.NodePayload;
import eu.valawai.mov.events.topology.QueryConnectionsPayload;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetConnectionsPagePayload}.
 *
 * @see GetConnectionsPagePayload
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetConnectionsPagePayloadTest extends MasterOfValawaiTestCase {

	/**
	 * Create some topology connections that can be used.
	 */
	@BeforeAll
	public static void createConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseNoOneMatchThePattern() {

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withSourceChannelName(
				"undefined source channel name that has not match any possible topology connection"));
		assertNull(page.queryId);
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.connections);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var offset = Integer.MAX_VALUE;
		final var total = this.assertItemNotNull(TopologyConnectionEntity.count());
		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withOffset(offset));
		assertNull(page.queryId);
		assertEquals(total, page.total);
		assertEquals(Collections.EMPTY_LIST, page.connections);

	}

	/**
	 * Test get a connection page.
	 */
	@Test
	public void shouldReturnPage() {

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var max = query.offset + query.limit + 10;
		final var filter = Filters.empty();

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;
		expected.total = nextTopologyConnectionsUntil(filter, max);

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

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a source channel.
	 *
	 * @see NodePayload#channelName
	 */
	@Test
	public void shouldReturnPageWithSourceChannelName() {

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		query.order = "-source.channelName";
		final var channelNamePattern = ".+c1_.+";
		query.sourceChannelName = "/" + channelNamePattern + "/";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.regex("source.channelName", channelNamePattern);
		expected.total = nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l2.source.channelName.compareTo(l1.source.channelName);
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

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a target channel.
	 *
	 * @see NodePayload#channelName
	 */
	@Test
	public void shouldReturnPageWithTargetChannelName() {

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		query.order = "-target.channelName";
		final var channelNamePattern = ".+c1_.+";
		query.targetChannelName = "/" + channelNamePattern + "/";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.regex("target.channelName", channelNamePattern);
		expected.total = nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = l2.target.channelName.compareTo(l1.target.channelName);
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

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a source component.
	 *
	 * @see NodePayload#componentId
	 */
	@Test
	public void shouldReturnPageWithSourceComponentId() {

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		query.order = "-source.componentId";
		final var componentId1 = nextObjectId();
		final var componentId2 = nextObjectId();
		final var componentIdPattern = componentId1.toHexString() + "|" + componentId2.toHexString();
		query.sourceComponentId = "/" + componentIdPattern + "/";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var connections = nextTopologyConnections(max);
		for (final var connection : connections) {

			if (flipCoin()) {

				connection.source.componentId = componentId1;

			} else {

				connection.source.componentId = componentId2;
			}

			this.assertItemNotNull(connection.update());
		}
		expected.total = max;
		connections.sort((l1, l2) -> {

			var cmp = l2.source.componentId.compareTo(l1.source.componentId);
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

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a target component.
	 *
	 * @see NodePayload#componentId
	 */
	@Test
	public void shouldReturnPageWithTargetComponentId() {

		final var query = new QueryConnectionsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		query.order = "-target.componentId";
		final var componentId1 = nextObjectId();
		final var componentId2 = nextObjectId();
		final var componentIdPattern = componentId1.toHexString() + "|" + componentId2.toHexString();
		query.targetComponentId = "/" + componentIdPattern + "/";

		final var expected = new ConnectionsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var connections = nextTopologyConnections(max);
		for (final var connection : connections) {

			if (flipCoin()) {

				connection.target.componentId = componentId1;

			} else {

				connection.target.componentId = componentId2;
			}

			this.assertItemNotNull(connection.update());
		}
		expected.total = max;
		connections.sort((l1, l2) -> {

			var cmp = l2.target.componentId.compareTo(l1.target.componentId);
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

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page with only one component.
	 */
	@Test
	public void shouldReturnPageWithOnlyOneConnection() {

		final var connection = TopologyConnectionEntities.nextTopologyConnection();

		final var query = new QueryConnectionsPayload();
		query.sourceComponentId = connection.source.componentId.toHexString();
		query.sourceChannelName = connection.source.channelName;
		query.targetComponentId = connection.target.componentId.toHexString();
		query.targetChannelName = connection.target.channelName;

		final var expected = new ConnectionsPagePayload();
		expected.total = 1;
		expected.connections = new ArrayList<>();
		expected.connections.add(ConnectionPayloadTest.from(connection));

		final var page = this.assertExecutionNotNull(GetConnectionsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

}
