/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextUUID;
import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.components.ComponentPayloadTest;
import eu.valawai.mov.events.components.ComponentsPagePayload;
import eu.valawai.mov.events.components.QueryComponentsPayload;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.components.GetComponentsPagePayload;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetComponentsPagePayload}.
 *
 * @see GetComponentsPagePayload
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetComponentsPagePayloadTest extends MasterOfValawaiTestCase {

	/**
	 * Create some components that can be used.
	 */
	@BeforeAll
	public static void createComponents() {

		for (var i = 0; i < 101; i += 10) {

			final var finished = ComponentEntities.nextComponent();
			finished.finishedTime = TimeManager.now();
			finished.update().await().atMost(Duration.ofSeconds(30));

			ComponentEntities.minComponents(i);
		}
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseNoOneMatchThePattern() {

		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh()
				.withPattern("undefined Pattern that has not match any possible component"));
		assertNull(page.queryId);
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var offset = Integer.MAX_VALUE;
		final var total = this.assertItemNotNull(ComponentEntity.mongoCollection()
				.countDocuments(Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null))));
		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh().withOffset(offset));
		assertNull(page.queryId);
		assertEquals(total, page.total);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get a component page.
	 */
	@Test
	public void shouldReturnPage() {

		final var query = new QueryComponentsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var max = query.offset + query.limit + 10;
		final var filter = Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null));

		final var expected = new ComponentsPagePayload();
		expected.queryId = query.id;
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> l1.id.compareTo(l2.id));
		expected.components = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = ComponentPayloadTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match some types.
	 *
	 * @see ComponentType
	 */
	@Test
	public void shouldReturnPageWithType() {

		final var query = new QueryComponentsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		query.order = "-type";
		final var type1 = next(ComponentType.values());
		var type2 = next(ComponentType.values());
		while (type1 == type2) {

			type2 = next(ComponentType.values());
		}
		query.type = "/" + type1.name() + "|" + type2.name() + "/";

		final var expected = new ComponentsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
				Filters.or(Filters.eq("type", type1), Filters.eq("type", type2)));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l2.type.name().compareTo(l1.type.name());
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = ComponentPayloadTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var query = new QueryComponentsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var pattern = ".*1.*";
		query.pattern = "/" + pattern + "/";
		query.order = "-name";

		final var expected = new ComponentsPagePayload();
		expected.queryId = query.id;

		final var max = query.offset + query.limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)),
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l2.name.compareTo(l1.name);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = query.offset; i < query.offset + query.limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = ComponentPayloadTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns¡ and a type.
	 */
	@Test
	public void shouldReturnPageWithPatternAndType() {

		final var query = new QueryComponentsPayload();
		query.id = nextUUID().toString();
		query.offset = rnd().nextInt(2, 5);
		query.limit = rnd().nextInt(5, 11);
		final var pattern = ".*1.*";
		query.pattern = "/" + pattern + "/";
		query.order = "description,-name";
		query.offset = ValueGenerator.rnd().nextInt(2, 5);
		final var type = ValueGenerator.next(ComponentType.values());
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

		final var page = this.assertExecutionNotNull(GetComponentsPagePayload.fresh().withQuery(query));
		assertEquals(expected, page);

	}

}
