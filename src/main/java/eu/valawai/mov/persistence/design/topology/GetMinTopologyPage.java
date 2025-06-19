/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import eu.valawai.mov.api.v2.design.topologies.MinTopologyPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Get {@link MinTopologyPage} from the data base.
 *
 * @see MinTopologyPage
 *
 * @author VALAWAI
 */
public class GetMinTopologyPage extends AbstractGetPage<MinTopologyPage, GetMinTopologyPage> {

	/**
	 * Create a new operation.
	 */
	private GetMinTopologyPage() {

		super("topologies");
	}

	/**
	 * Create the operation to obtain some topologies.
	 *
	 * @return the new get page operation.
	 */
	public static GetMinTopologyPage fresh() {

		return new GetMinTopologyPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Bson> createPipelineBeforeFacet() {

		final var pipeline = super.createPipelineBeforeFacet();
		pipeline.add(Aggregates.project(Projections.include("_id", "name", "description")));
		return pipeline;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		if (this.pattern != null) {

			return Filters.or(Queries.filterByValueOrRegexp("name", this.pattern),
					Queries.filterByValueOrRegexp("description", this.pattern));

		} else {

			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<MinTopologyPage> getPageWith(List<Bson> pipeline) {

		return TopologyGraphEntity.mongoCollection().aggregate(pipeline, MinTopologyPage.class).collect().first()
				.onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some topologies");
					final var page = new MinTopologyPage();
					page.offset = this.offset;
					return page;
				});

	}

}
