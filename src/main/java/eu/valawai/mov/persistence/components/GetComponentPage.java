/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.ComponentPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a page with some components.
 *
 * @see ComponentPage
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public class GetComponentPage extends AbstractGetPage<ComponentPage, GetComponentPage> {

	/**
	 * Create a new operation.
	 */
	private GetComponentPage() {

		super("components");
	}

	/**
	 * Create the operation to obtain some components.
	 *
	 * @return the new get page operation.
	 */
	public static GetComponentPage fresh() {

		return new GetComponentPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<ComponentPage> getPageWith(List<Bson> pipeline) {

		return ComponentEntity.mongoCollection().aggregate(pipeline, ComponentPage.class).collect().first().onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some components");
					final var page = new ComponentPage();
					page.offset = this.offset;
					return page;
				});
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

}
