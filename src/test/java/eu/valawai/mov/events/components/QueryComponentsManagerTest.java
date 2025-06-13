/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import static eu.valawai.mov.ValueGenerator.next;
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

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.events.MovEventTestCase;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.logs.LogEntity;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.Json;

/**
 * Test the {@link QueryComponentsManager}.
 *
 * @see QueryComponentsManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class QueryComponentsManagerTest extends MovEventTestCase {

	/**
	 * The name of the queue to send the query to obtain some components.
	 */
	@ConfigProperty(name = "mp.messaging.incoming.query_components.queue.name", defaultValue = "valawai/component/query")
	String queryComponentstQueueName;

	/**
	 * The name of the queue to send the query to obtain some components.
	 */
	@ConfigProperty(name = "mp.messaging.outgoing.components_page.queue.name", defaultValue = "valawai/component/page")
	String componentsPagetQueueName;

	/**
	 * Check that cannot ask for components with an invalid payload.
	 */
	@Test
	public void shouldNotQueryComponentsWithInvalidPayload() {

		final var payload = new QueryComponentsPayload();
		payload.offset = -1;

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryComponentstQueueName, payload));

		final LogEntity log = this.assertItemNotNull(LogEntity.findAll(Sort.descending("_id")).firstResult());
		assertEquals(LogLevel.ERROR, log.level);
		final var logPayload = Json.decodeValue(log.payload, QueryComponentsPayload.class);
		assertEquals(payload, logPayload);
	}

	/**
	 * Check that query return an empty page.
	 */
	@Test
	public void shouldQueryReturnrEmptyPage() {

		final var queue = this.waitOpenQueue(this.componentsPagetQueueName);

		final var payload = new QueryComponentsPayload();
		payload.id = nextUUID().toString();
		payload.offset = 3;
		payload.limit = 7;
		payload.pattern = nextUUID().toString();

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryComponentstQueueName, payload));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + payload.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));
		final var msg = queue.waitReceiveMessage(ComponentsPagePayload.class);
		assertEquals(payload.id, msg.queryId);
		assertEquals(0l, msg.total);
		assertNull(msg.components);

	}

	/**
	 * Check that query with pattern and type.
	 */
	@Test
	public void shouldQueryWithPatternAndType() {

		final var queue = this.waitOpenQueue(this.componentsPagetQueueName);

		final var query = new QueryComponentsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var pattern = ".*1.*";
		query.pattern = "/" + pattern + "/";
		query.order = "description,-name";
		query.offset = rnd().nextInt(2, 5);
		final var type = next(ComponentType.values());
		query.type = type.name();

		final var expected = new ComponentsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)),
				Filters.eq("type", type));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l1.description.compareTo(l2.description);
			if (cmp == 0) {

				cmp = l2.name.compareTo(l1.name);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = ComponentPayloadTest.from(component);
			expected.components.add(expectedComponent);
		}

		this.executeAndWaitUntilNewLog(() -> this.assertPublish(this.queryComponentstQueueName, query));

		assertEquals(1l, LogEntity.count("level = ?1 and message like ?2", LogLevel.INFO, ".+" + query.id + ".*")
				.await().atMost(Duration.ofSeconds(30)));

		final var page = queue.waitReceiveMessage(ComponentsPagePayload.class);
		assertEquals(expected, page);

	}

}
