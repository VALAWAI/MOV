/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.MinComponentPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a page with some components.
 *
 * @see MinComponentPage
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public class GetMinComponentPage extends AbstractGetPage<MinComponentPage, GetMinComponentPage> {

	/**
	 * The type to match the component to return.
	 */
	protected String type;

	/**
	 * Create a new operation.
	 */
	private GetMinComponentPage() {

		super("components");
	}

	/**
	 * Create the operation to obtain some components.
	 *
	 * @return the new get page operation.
	 */
	public static GetMinComponentPage fresh() {

		return new GetMinComponentPage();
	}

	/**
	 * The type to match the page components.
	 *
	 * @param type to match the components to return.
	 *
	 * @return this operator.
	 */
	public GetMinComponentPage withType(final String type) {

		this.type = type;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<MinComponentPage> getPageWith(List<Bson> pipeline) {

		return ComponentEntity.mongoCollection().aggregate(pipeline, MinComponentPage.class).collect().first()
				.onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some components");
					final var page = new MinComponentPage();
					page.offset = this.offset;
					return page;
				});
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
