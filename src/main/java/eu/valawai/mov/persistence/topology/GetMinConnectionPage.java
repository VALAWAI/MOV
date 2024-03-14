/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

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
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<MinConnectionPage> getPageWith(List<Bson> pipeline) {

		final var copy = new ArrayList<>(pipeline);
		copy.add(Aggregates.set(new Field<>("sourceComponentId", new Document("$toString", "$source.componentId")),
				new Field<>("targetComponentId", new Document("$toString", "$target.componentId")),
				new Field<>("source", "$source.channelName"), new Field<>("target", "$target.channelName")));
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

		return Filters.or(Queries.filterByValueOrRegexp("source.channelId", this.pattern),
				Queries.filterByValueOrRegexp("sourceComponentId", this.pattern),
				Queries.filterByValueOrRegexp("target.channelId", this.pattern),
				Queries.filterByValueOrRegexp("targetComponentId", this.pattern));
	}

}
