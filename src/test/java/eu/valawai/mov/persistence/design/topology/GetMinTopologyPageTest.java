/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.mov.persistence.design.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.v2.design.topologies.MinTopologyPage;
import eu.valawai.mov.api.v2.design.topologies.MinTopologyTest;
import eu.valawai.mov.persistence.MovPersistenceTestCase;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link GetMinTopologyPage}.
 *
 * @see GetMinTopologyPage
 *
 * @author VALAWAI
 */
@QuarkusTest
public class GetMinTopologyPageTest extends MovPersistenceTestCase {

	/**
	 * Create some MinTopologys that can be used.
	 */
	@BeforeAll
	public static void createMinTopologys() {

		TopologyGraphEntities.minTopologies(100);
	}

	/**
	 * Test get an empty page because no one match the pattern.
	 */
	@Test
	public void shouldReturnEmptyPageBecausenopAnyoneMatchThePattern() {

		final var page = this.assertExecutionNotNull(GetMinTopologyPage.fresh()
				.withPattern("undefined Pattern that has not match any possible MinTopology"));
		assertEquals(0l, page.total);
		assertEquals(Collections.EMPTY_LIST, page.topologies);

	}

	/**
	 * Test get an empty page because the offset is too large.
	 */
	@Test
	public void shouldReturnEmptyPageBecauseOffsetTooLarge() {

		final var total = this.assertItemNotNull(TopologyGraphEntity.mongoCollection().countDocuments());
		final int offset = (int) (total + 1);
		final var page = this.assertItemNotNull(GetMinTopologyPage.fresh().withOffset(offset).execute());
		assertEquals(total, page.total);
		assertEquals(offset, page.offset);
		assertEquals(Collections.EMPTY_LIST, page.topologies);

	}

	/**
	 * Test get a page that match a patterns.
	 */
	@Test
	public void shouldReturnPageWithPattern() {

		final var pattern = ".*1.*";

		final var expected = new MinTopologyPage();
		expected.offset = ValueGenerator.rnd().nextInt(2, 5);

		final var limit = ValueGenerator.rnd().nextInt(5, 11);
		final var max = expected.offset + limit + 10;
		final var filter = Filters
				.and(Filters.or(Filters.regex("name", pattern), Filters.regex("description", pattern)));
		expected.total = TopologyGraphEntities.nextTopologyGraphsUntil(filter, max);

		final List<TopologyGraphEntity> topologies = this.assertItemNotNull(
				TopologyGraphEntity.mongoCollection().find(filter, TopologyGraphEntity.class).collect().asList());
		topologies.sort((l1, l2) -> {

			var cmp = l2.description.compareTo(l1.description);
			if (cmp == 0) {

				cmp = l1.id.compareTo(l2.id);
			}

			return cmp;
		});
		expected.topologies = new ArrayList<>();
		for (int i = expected.offset; i < expected.offset + limit && i < topologies.size(); i++) {

			final var topology = topologies.get(i);
			final var expectedMinTopology = MinTopologyTest.from(topology);
			expected.topologies.add(expectedMinTopology);
		}

		final var page = this.assertExecutionNotNull(GetMinTopologyPage.fresh().withPattern("/" + pattern + "/")
				.withOrder("-description").withOffset(expected.offset).withLimit(limit));
		assertEquals(expected, page);

	}

}
