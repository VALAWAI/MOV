/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.MinComponentPage;
import eu.valawai.mov.api.v1.components.MinComponentTest;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the operation to get some components.
 *
 * @see GetMinComponentPage
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetMinComponentPageTest extends MasterOfValawaiTestCase {

	/**
	 * Create some components that can be used.
	 */
	@BeforeClass
	public static void createComponents() {

		ComponentEntities.minComponents(100);
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecausenopOneMatchThePattern() {

		final var page = this.assertExecutionNotNull(
				GetMinComponentPage.fresh().withPattern("undefined Pattern that has not match any possible component"));
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsettooLarge() {

		final var offset = Integer.MAX_VALUE;
		final var total = this.assertItemNotNull(ComponentEntity.count());
		final var page = this.assertExecutionNotNull(GetMinComponentPage.fresh().withOffset(offset));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get a component page.
	 */
	@Test
	public void shouldReturnPage() {

		final var expected = new MinComponentPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.empty();
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> l1.id.compareTo(l2.id));
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this
				.assertExecutionNotNull(GetMinComponentPage.fresh().withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match some types.
	 *
	 * @see ComponentType
	 */
	@Test
	public void shouldReturnPageWithType() {

		final var type1 = ValueGenerator.next(ComponentType.values());
		var type2 = ValueGenerator.next(ComponentType.values());
		while (type1 == type2) {

			type2 = ValueGenerator.next(ComponentType.values());
		}

		final var expected = new MinComponentPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.or(Filters.eq("type", type1), Filters.eq("type", type2));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l2.type.compareTo(l1.type);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(
				GetMinComponentPage.fresh().withType("/" + type1.name() + "|" + type2.name() + "/").withOrder("-type")
						.withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new MinComponentPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern));
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
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(GetMinComponentPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-name").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns¡ and a type.
	 */
	@Test
	public void shouldReturnPageWithPatternAndType() {

		final var pattern = ".*1.*";
		final var type = ValueGenerator.next(ComponentType.values());

		final var expected = new MinComponentPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)),
				Filters.eq("type", type));
		expected.total = ComponentEntities.nextComponentsUntil(filter, max);

		final List<ComponentEntity> components = this.assertItemNotNull(
				ComponentEntity.mongoCollection().find(filter, ComponentEntity.class).collect().asList());
		components.sort((l1, l2) -> {

			var cmp = l2.type.compareTo(l1.type);
			if (cmp == 0) {

				cmp = l1.description.compareTo(l2.description);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < components.size(); i++) {

			final var component = components.get(i);
			final var expectedComponent = MinComponentTest.from(component);
			expected.components.add(expectedComponent);
		}

		final var page = this.assertExecutionNotNull(GetMinComponentPage.fresh().withPattern("/" + pattern + "/")
				.withType(type.name()).withOrder("-type,description").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

}
