/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentPage;
import eu.valawai.mov.api.v1.components.ComponentTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link ComponentRepository}.
 *
 * @see ComponentRepository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class ComponentRepositoryTest extends MovPersistenceTestCase {

	/**
	 * The repository to test.
	 */
	@Inject
	ComponentRepository repository;

	/**
	 * Should not add {@code null} component.
	 */
	@Test
	public void shouldNotAddNullComponent() {

		assertFalse(this.repository.add(null));
	}

	/**
	 * Should add a component.
	 */
	@Test
	public void shouldAddComponents() {

		final var component = new ComponentTest().nextModel();
		final var now = TimeManager.now();
		final var count = this.repository.count();
		assertTrue(this.repository.add(component));
		final var last = this.repository.last();
		assertNotNull(last.id);
		assertTrue(last.since >= now);
		assertEquals(count + 1, this.repository.count());
		component.since = last.since;
		component.id = last.id;
		assertEquals(component, last);

	}

	/**
	 * Should clear components.
	 */
	@Test
	public void shouldClearComponents() {

		this.repository.clear();
		assertEquals(0, this.repository.count());
		assertNull(this.repository.last());
		final var emptyPage = new ComponentPage();
		assertEquals(emptyPage, this.repository.getComponentPage(null, null, 0, 20));
		emptyPage.offset = 3;
		assertEquals(emptyPage, this.repository.getComponentPage(null, ",", emptyPage.offset, 20));
	}

	/**
	 * Should add multiple component.
	 */
	@Test
	public void shouldAddMultipleComponents() {

		this.repository.clear();
		final var builder = new ComponentTest();
		for (var i = 0; i < 10; i++) {

			final var component = builder.nextModel();
			final var now = TimeManager.now();
			final var count = this.repository.count();
			assertTrue(this.repository.add(component));
			final var last = this.repository.last();
			assertNotNull(last.id);
			assertEquals(String.valueOf(i + 1), last.id);
			assertTrue(last.since >= now);
			assertEquals(count + 1, this.repository.count());
			component.since = last.since;
			component.id = last.id;
			assertEquals(component, last);

		}

	}

	/**
	 * Should get a page.
	 */
	@Test
	public void shouldGetAPage() {

		final var expected = new ComponentPage();
		final var all = new ArrayList<Component>();
		expected.components = all;
		expected.total = 20;
		this.repository.clear();
		final var builder = new ComponentTest();
		for (var i = 0; i < expected.total; i++) {

			assertTrue(this.repository.add(builder.nextModel()));
			final var last = this.repository.last();
			expected.components.add(last);

		}

		assertEquals(expected, this.repository.getComponentPage(null, null, 0, all.size()));

		expected.components = null;
		expected.offset = all.size();
		assertEquals(expected, this.repository.getComponentPage(null, null, expected.offset, all.size()));

		expected.components = all.subList(1, 4);
		expected.offset = 1;
		assertEquals(expected, this.repository.getComponentPage(null, null, expected.offset, 3));

		expected.components = all;
		expected.offset = 0;
		expected.components.sort((c1, c2) -> {

			var cmp = Long.compare(c1.since, c2.since);
			if (cmp == 0) {

				cmp = c1.type.compareTo(c2.type);
				if (cmp == 0) {

					cmp = c1.name.compareTo(c2.name);
				}
			}
			return cmp;
		});
		assertEquals(expected, this.repository.getComponentPage(null, "since,type,name", 0, all.size()));

		expected.components.sort((c1, c2) -> {

			var cmp = Long.compare(c2.since, c1.since);
			if (cmp == 0) {

				cmp = c2.type.compareTo(c1.type);
				if (cmp == 0) {

					cmp = c2.name.compareTo(c1.name);
				}
			}
			return cmp;
		});
		assertEquals(expected,
				this.repository.getComponentPage(null, "-since,-type,-name,payload", 0, expected.components.size()));

		expected.components = all.stream().filter(l -> l.name.matches(".*1.*")).toList();
		expected.total = expected.components.size();
		assertEquals(expected, this.repository.getComponentPage("/.*1.*/", "-since,-type,-name", 0, all.size()));

		expected.offset = 1;
		if (expected.total > 5) {

			expected.components = expected.components.subList(1, 5);
			assertEquals(expected,
					this.repository.getComponentPage("/.*1.*/", "-since,-type,-name", expected.offset, 4));
		}

		expected.offset = 1;
		expected.components = all.subList(expected.offset, 5);
		expected.total = all.size();
		assertEquals(expected, this.repository.getComponentPage("/.*/", "-since,-type,-name", expected.offset, 4));

	}

}
