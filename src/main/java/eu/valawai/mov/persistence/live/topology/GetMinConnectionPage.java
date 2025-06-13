/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.topology.MinConnectionPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a page with some connections.
 *
 * @see MinConnectionPage
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class GetMinConnectionPage extends AbstractGetPage<MinConnectionPage, GetMinConnectionPage> {

	/**
	 * The component to match the model has returned.
	 */
	protected String component;

	/**
	 * Create a new operation.
	 */
	private GetMinConnectionPage() {

		super("connections");
	}

	/**
	 * Create the operation to obtain some connections.
	 *
	 * @return the new get page operation.
	 */
	public static GetMinConnectionPage fresh() {

		return new GetMinConnectionPage();
	}

	/**
	 * The component to match the page elements.
	 *
	 * @param component to match the elements to return.
	 *
	 * @return this operator.
	 */
	public GetMinConnectionPage withComponent(final String component) {

		this.component = component;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<MinConnectionPage> getPageWith(List<Bson> pipeline) {

		final var copy = new ArrayList<>(pipeline);

		copy.add(0, Aggregates.set(new Field<>("source", "$source.channelName"),
				new Field<>("target", "$target.channelName")));

		if (this.component != null) {

			copy.add(0,
					Aggregates.set(new Field<>("sourceComponentId", new Document("$toString", "$source.componentId")),
							new Field<>("targetComponentId", new Document("$toString", "$target.componentId"))));
		}

		return TopologyConnectionEntity.mongoCollection().aggregate(copy, MinConnectionPage.class).collect().first()
				.onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some connections");
					final var page = new MinConnectionPage();
					page.offset = this.offset;
					return page;
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		final var filters = new ArrayList<Bson>();
		filters.add(Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		if (this.component != null) {

			filters.add(Filters.or(Queries.filterByValueOrRegexp("sourceComponentId", this.component),
					Queries.filterByValueOrRegexp("targetComponentId", this.component)));
		}

		if (this.pattern != null) {

			filters.add(Filters.or(Queries.filterByValueOrRegexp("source", this.pattern),
					Queries.filterByValueOrRegexp("target", this.pattern)));

		}
		return Filters.and(filters);
	}

}
