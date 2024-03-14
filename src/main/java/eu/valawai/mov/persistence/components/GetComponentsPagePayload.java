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

import eu.valawai.mov.events.components.ComponentsPagePayload;
import eu.valawai.mov.events.components.QueryComponentsPayload;
import io.smallrye.mutiny.Uni;

/**
 * Operator to obtain the {@link ComponentsPagePayload}.
 *
 * @see ComponentsPagePayload
 *
 * @author VALAWAI
 */
public class GetComponentsPagePayload
		extends AbstractGetPageComponents<ComponentsPagePayload, GetComponentsPagePayload> {

	/**
	 * The identifier of the query.
	 */
	protected String queryId;

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

}
