/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;

/**
 * The generic get components page operator.
 *
 * @param <R> type of page that it obtains.
 * @param <O> type of the operator.
 *
 * @author VALAWAI
 */
public abstract class AbstractGetPageComponents<R, O extends AbstractGetPageComponents<R, O>>
		extends AbstractGetPage<R, O> {

	/**
	 * The type to match the components to be returned.
	 */
	protected String type;

	/**
	 * Create a new get page operator.
	 *
	 * @param fieldName field name to store the models of the page.
	 */
	protected AbstractGetPageComponents(String fieldName) {

		super(fieldName);

	}

	/**
	 * The type to match the components.
	 *
	 * @param type to match the components to return.
	 *
	 * @return this operator.
	 */
	public O withType(final String type) {

		this.type = type;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		final var filters = this.createComponentsFilters();
		return Filters.and(filters);

	}

	/**
	 * Create the filters to get the components for the page.
	 *
	 * @return the filters to select the component that has not finished and match
	 *         the pattern and the type.
	 */
	protected List<Bson> createComponentsFilters() {

		final var filters = new ArrayList<Bson>();
		filters.add(Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)));
		if (this.pattern != null) {

			filters.add(Filters.or(Queries.filterByValueOrRegexp("name", this.pattern),
					Queries.filterByValueOrRegexp("description", this.pattern)));

		}
		if (this.type != null) {

			filters.add(Queries.filterByValueOrRegexp("type", this.type));
		}
		return filters;
	}

}
