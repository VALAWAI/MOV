/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.mov.persistence.design.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v2.design.components.ComponentDefinitionPage;
import eu.valawai.mov.api.v2.design.components.ComponentDefinitionTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetComponentDefinitionPage}.
 *
 * @see GetComponentDefinitionPage
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetComponentDefinitionPageTest extends MovPersistenceTestCase {

	/**
	 * Create some ComponentDefinitions that can be used.
	 */
	@BeforeAll
	public static void createComponentDefinitions() {

		ComponentDefinitionEntities.minComponents(100);
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecausenopAnyoneMatchThePattern() {

		final var page = this.assertExecutionNotNull(GetComponentDefinitionPage.fresh()
				.withPattern("undefined Pattern that has not match any possible ComponentDefinition"));
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var total = this.assertItemNotNull(ComponentDefinitionEntity.mongoCollection().countDocuments());
		final int offset = (int) (total + 1);
		final var page = this.assertItemNotNull(GetComponentDefinitionPage.fresh().withOffset(offset).execute());
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get a page that match some type.
	 *
	 * @param type to match.
	 */
	@ParameterizedTest(name = "Check that get a page that match type {0}")
	@EnumSource(ComponentType.class)
	public void shouldReturnPageWithType(ComponentType type) {

		final var expected = new ComponentDefinitionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.eq("type", type.name());
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> ComponentDefinitions = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
		ComponentDefinitions.sort((l1, l2) -> l1.id.compareTo(l2.id));
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < ComponentDefinitions.size(); i++) {

			final var ComponentDefinition = ComponentDefinitions.get(i);
			final var expectedComponentDefinition = ComponentDefinitionTest.from(ComponentDefinition);
			expected.components.add(expectedComponentDefinition);
		}

		final var page = this.assertExecutionNotNull(
				GetComponentDefinitionPage.fresh().withType(type).withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a type pattern.
	 */
	@Test
	public void shouldReturnPageWithTypePattern() {

		final var expected = new ComponentDefinitionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.regex("type", "C[0|2]");
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> ComponentDefinitions = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
		ComponentDefinitions.sort((l1, l2) -> {

			var cmp = l2.name.compareTo(l1.name);
			if (cmp == 0) {

				cmp = l1.description.compareTo(l2.description);
				if (cmp == 0) {

					cmp = l1.id.compareTo(l2.id);
				}
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < ComponentDefinitions.size(); i++) {

			final var ComponentDefinition = ComponentDefinitions.get(i);
			final var expectedComponentDefinition = ComponentDefinitionTest.from(ComponentDefinition);
			expected.components.add(expectedComponentDefinition);
		}

		final var page = this.assertExecutionNotNull(GetComponentDefinitionPage.fresh().withType("/C[0|2]/")
				.withOrder("-name,description").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new ComponentDefinitionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters
				.and(Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)));
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> ComponentDefinitions = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
		ComponentDefinitions.sort((l1, l2) -> {

			var cmp = l2.description.compareTo(l1.description);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.components = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < ComponentDefinitions.size(); i++) {

			final var ComponentDefinition = ComponentDefinitions.get(i);
			final var expectedComponentDefinition = ComponentDefinitionTest.from(ComponentDefinition);
			expected.components.add(expectedComponentDefinition);
		}

		final var page = this.assertExecutionNotNull(GetComponentDefinitionPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-description").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

	/**
	 * Test get a page that match a patterns¡ and a type.
	 */
	@Test
	public void shouldReturnPageWithPatternAndType() {

		final var pattern = ".*1.*";
		final var type = ValueGenerator.next(ComponentType.values());

		final var expected = new ComponentDefinitionPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters.and(
				Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)),
				Filters.eq("type", type));
		expected.total = ComponentDefinitionEntities.nextComponentDefinitionsUntil(filter, max);

		final List<ComponentDefinitionEntity> ComponentDefinitions = this.assertItemNotNull(ComponentDefinitionEntity
				.mongoCollection().find(filter, ComponentDefinitionEntity.class).collect().asList());
		ComponentDefinitions.sort((l1, l2) -> {

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
		for (int i = expected.offset; i < expected.offset + limit && i < ComponentDefinitions.size(); i++) {

			final var ComponentDefinition = ComponentDefinitions.get(i);
			final var expectedComponentDefinition = ComponentDefinitionTest.from(ComponentDefinition);
			expected.components.add(expectedComponentDefinition);
		}

		final var page = this.assertExecutionNotNull(GetComponentDefinitionPage.fresh().withPattern("/" + pattern + "/")
				.withType(type.name()).withOrder("description,-name").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

}
