/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.ValueGenerator;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the operation to get some components.
 *
 * @see GetComponentPage
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class GetComponentPageTest extends MasterOfValawaiTestCase {

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
				GetComponentPage.fresh().withPattern("undefined Pattern that has not match any possible component"));
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
		final var page = this.assertExecutionNotNull(GetComponentPage.fresh().withOffset(offset));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.components);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnPage() {

		final var total = this.assertItemNotNull(ComponentEntity.count());
		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var page = this.assertExecutionNotNull(GetComponentPage.fresh().withOffset(offset).withLimit(limit));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var components = this
				.assertItemNotNull(ComponentEntity.findAll(Sort.ascending("_id")).page(Page.of(offset, limit)).list());
		assertEquals(components, page.components);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";
		final var total = this.assertItemNotNull(ComponentEntity.count("name like ?1 or description like ?1", pattern));
		final var offset = ValueGenerator.rnd().nextInt(2, 5);
		final var limit = ValueGenerator.rnd().nextInt(2, 5);
		final var page = this.assertExecutionNotNull(GetComponentPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-name").withOffset(offset).withLimit(limit));
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		final var components = this.assertItemNotNull(ComponentEntity
				.find("name like ?1 or description like ?1",
						Sort.descending("name").and("_id", Sort.Direction.Ascending), pattern)
				.page(Page.of(offset, limit)).list());
		assertEquals(components, page.components);

	}

}
