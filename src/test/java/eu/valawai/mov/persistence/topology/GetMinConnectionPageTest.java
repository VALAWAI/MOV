/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.topology.MinConnectionPage;
import eu.valawai.mov.api.v1.topology.MinConnectionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import eu.valawai.mov.persistence.components.ComponentEntities;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the operation to get some connections.
 *
 * @see GetMinConnectionPage
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetMinConnectionPageTest extends MovPersistenceTestCase {

	/**
	 * Create some connections that can be used.
	 */
	@BeforeAll
	public static void createConnections() {

		for (var i = 0; i < 101; i += 10) {

			final var finished = TopologyConnectionEntities.nextTopologyConnection();
			finished.deletedTimestamp = TimeManager.now();
			finished.update().await().atMost(Duration.ofSeconds(30));

			TopologyConnectionEntities.minTopologyConnections(i);
		}
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecausenopOneMatchThePattern() {

		final var page = this.assertExecutionNotNull(GetMinConnectionPage.fresh()
				.withPattern("undefined Pattern that has not match any possible connection"));
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.connections);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var offset = Integer.MAX_VALUE;
		final var total = this.assertItemNotNull(TopologyConnectionEntity.mongoCollection().countDocuments(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null))));
		final var page = this.assertExecutionNotNull(GetMinConnectionPage.fresh().withOffset(offset));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.connections);

	}

	/**
	 * Test get a connection page.
	 */
	@Test
	public void shouldReturnPage() {

		final var expected = new MinConnectionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> l1.id.compareTo(l2.id));
		expected.connections = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = this
				.assertExecutionNotNull(GetMinConnectionPage.fresh().withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new MinConnectionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.regex("source.channelName", pattern), Filters.regex("target.channelName", pattern)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

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
		for (int i = expected.offset; i < expected.offset + limit && i < connections.size(); i++) {

			final var connection = connections.get(i);
			final var expectedConnection = MinConnectionTest.from(connection);
			expected.connections.add(expectedConnection);
		}

		final var page = this.assertExecutionNotNull(GetMinConnectionPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-source").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a component.
	 */
	@Test
	public void shouldReturnPageWithComponent() {

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

		final var page = this
				.assertExecutionNotNull(GetMinConnectionPage.fresh().withComponent(component.id.toHexString())
						.withOrder("target,-source").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a pattern and a component.
	 */
	@Test
	public void shouldReturnPageWithPatternAndComponent() {

		final var pattern = ".*_[1|2|3|4|5].*";
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

			if (connection.deletedTimestamp != null || !connection.source.channelName.matches(pattern)
					|| !connection.target.channelName.matches(pattern)) {

				i--;
			}
		}

		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.or(Filters.regex("source.channelName", pattern), Filters.regex("target.channelName", pattern)),
				Filters.or(Filters.eq("source.componentId", component.id),
						Filters.eq("target.componentId", component.id)));
		expected.total = TopologyConnectionEntities.nextTopologyConnectionsUntil(filter, max);

		final List<TopologyConnectionEntity> connections = this.assertItemNotNull(TopologyConnectionEntity
				.mongoCollection().find(filter, TopologyConnectionEntity.class).collect().asList());
		connections.sort((l1, l2) -> {

			var cmp = Boolean.compare(l2.enabled, l1.enabled);
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

		final var page = this.assertExecutionNotNull(
				GetMinConnectionPage.fresh().withPattern("/" + pattern + "/").withComponent(component.id.toHexString())
						.withOrder("-enabled,-source").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

}
