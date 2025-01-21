/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Projections;

import io.smallrye.mutiny.Uni;

/**
 * Generic operation used to obtain a page of something.
 *
 * @param <T> type of page that it obtains.
 * @param <O> type of the operator.
 *
 * @author VALAWAI
 */
public abstract class AbstractGetMinPage<T, O extends AbstractGetMinPage<T, O>> extends AbstractEntityOperator<T, O> {

	/**
	 * The order in with the model has returned.
	 */
	protected String order;

	/**
	 * The offset to the first model to return.
	 */
	protected int offset = 0;

	/**
	 * The number maximum of models to return.
	 */
	protected int limit = 10;

	/**
	 * The field name to store the models of the page.
	 */
	private final String fieldName;

	/**
	 * Create a new get page operator.
	 *
	 * @param fieldName field name to store the models of the page.
	 */
	protected AbstractGetMinPage(String fieldName) {

		this.fieldName = fieldName;

	}

	/**
	 * The order to return the page content.
	 *
	 * @param order to return the page elements.
	 *
	 * @return this operator.
	 */
	public O withOrder(final String order) {

		this.order = order;
		return this.operator();
	}

	/**
	 * The index of the first element to return.
	 *
	 * @param offset of the first element to return.
	 *
	 * @return this operator.
	 */
	public O withOffset(final int offset) {

		this.offset = offset;
		return this.operator();
	}

	/**
	 * The number maximum of elements in the page to return.
	 *
	 * @param limit of size of the page elements to return.
	 *
	 * @return this operator.
	 */
	public O withLimit(final int limit) {

		this.limit = limit;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<T> execute() {

		final var pipeline = this.createPipelineBeforeFacet();
		final var total = new Facet("total", Aggregates.count());
		final var logs = new Facet(this.fieldName, Aggregates.sort(Orders.orderBy(this.order)),
				Aggregates.skip(this.offset), Aggregates.limit(this.limit));

		pipeline.add(Aggregates.facet(total, logs));
		final var project = Aggregates
				.project(Projections.fields(Projections.computed("total", new Document("$first", "$total.count")),
						Projections.include(this.fieldName)));
		pipeline.add(project);
		final var offsetField = new Field<>("offset", this.offset);
		pipeline.add(Aggregates.addFields(offsetField));
		return this.getPageWith(pipeline);
	}

	/**
	 * Create the pipeline before the facet.
	 *
	 * @return the pipeline with the required filters.
	 */
	protected List<Bson> createPipelineBeforeFacet() {

		final var pipeline = new ArrayList<Bson>();
		final var filter = this.createFilter();
		if (filter != null) {

			pipeline.add(Aggregates.match(filter));
		}

		return pipeline;
	}

	/**
	 * Create an empty page to return when cannot obtain the page.
	 *
	 * @return an empty page.
	 */
	protected abstract Uni<T> getPageWith(List<Bson> pipeline);

	/**
	 * Create the filter to select the models to get on the page.
	 *
	 * @return the filter for the components of the page or {@code null} if not have
	 *         to filter.
	 */
	protected abstract Bson createFilter();
}
