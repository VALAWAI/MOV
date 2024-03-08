/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;

import eu.valawai.mov.events.components.ComponentsPagePayload;
import eu.valawai.mov.events.components.QueryComponentsPayload;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.smallrye.mutiny.Uni;

/**
 * Operator to obtain the {2link ComponentsPagePayload}.
 *
 * @see ComponentsPagePayload
 *
 * @author VALAWAI
 */
public class GetComponentsPagePayload extends AbstractGetPage<ComponentsPagePayload, GetComponentsPagePayload> {

	/**
	 * The identifier of the query.
	 */
	protected String queryId;

	/**
	 * The type to match the components to be returned.
	 */
	protected String type;

	/**
	 * Create the operator.
	 */
	private GetComponentsPagePayload() {

		super("components");
	}

	/**
	 * Create the operator to get the page.
	 *
	 * @return the operator to obtain the components page.
	 */
	public static GetComponentsPagePayload fresh() {

		return new GetComponentsPagePayload();
	}

	/**
	 * The type to match the components.
	 *
	 * @param type to match the components to return.
	 *
	 * @return this operator.
	 */
	public GetComponentsPagePayload withType(final String type) {

		this.type = type;
		return this.operator();
	}

	/**
	 * Specify the query to do.
	 *
	 * @param query to do.
	 *
	 * @return this operator.
	 */
	public GetComponentsPagePayload withQuery(QueryComponentsPayload query) {

		if (query != null) {

			this.queryId = query.id;
			this.withPattern(query.pattern);
			this.withType(query.type);
			this.withOrder(query.order);
			this.withOffset(query.offset);
			this.withLimit(query.limit);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<ComponentsPagePayload> getPageWith(List<Bson> pipeline) {

		if (this.queryId != null) {

			pipeline = new ArrayList<>(pipeline);
			pipeline.add(Aggregates.addFields(new Field<>("queryId", this.queryId)));
		}
		return ComponentEntity.mongoCollection().aggregate(pipeline, ComponentsPagePayload.class).collect().first();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		if (this.pattern != null && this.type != null) {

			return Filters.and(
					Filters.or(Queries.filterByValueOrRegexp("name", this.pattern),
							Queries.filterByValueOrRegexp("description", this.pattern)),
					Queries.filterByValueOrRegexp("type", this.type));

		} else if (this.pattern != null) {

			return Filters.or(Queries.filterByValueOrRegexp("name", this.pattern),
					Queries.filterByValueOrRegexp("description", this.pattern));

		} else if (this.type != null) {

			return Queries.filterByValueOrRegexp("type", this.type);

		} else {

			return null;
		}

	}

}
