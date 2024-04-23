/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import eu.valawai.mov.api.v1.logs.LogRecordPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import eu.valawai.mov.persistence.components.ComponentEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a page with some logs.
 *
 * @see LogRecordPage
 * @see LogEntity
 *
 * @author VALAWAI
 */
public class GetLogRecordPage extends AbstractGetPage<LogRecordPage, GetLogRecordPage> {

	/**
	 * The expected level of the logs to obtain.
	 */
	protected String level;

	/**
	 * The expected component type of the logs to obtain.
	 */
	protected String componentType;

	/**
	 * The expected component name or description of the logs to obtain.
	 */
	protected String componentPattern;

	/**
	 * Create a new operation.
	 */
	private GetLogRecordPage() {

		super("logs");
	}

	/**
	 * Create the operation to obtain some logs.
	 *
	 * @return the new get page operation.
	 */
	public static GetLogRecordPage fresh() {

		return new GetLogRecordPage();
	}

	/**
	 * The level to match the logs.
	 *
	 * @param level to match the logs.
	 *
	 * @return this operator.
	 */
	public GetLogRecordPage withLevel(final String level) {

		this.level = level;
		return this.operator();
	}

	/**
	 * Set the pattern that has to match the component name or description of the
	 * log.
	 *
	 * @param pattern to match the component name or description of the logs to
	 *                return.
	 *
	 * @return this operator.
	 */
	public GetLogRecordPage withComponnetPattern(String pattern) {

		this.componentPattern = pattern;
		return this.operator();
	}

	/**
	 * Set the type that has to match the component type of the log.
	 *
	 * @param type to match the component type of the logs to return.
	 *
	 * @return this operator.
	 */
	public GetLogRecordPage withComponnetType(String type) {

		this.componentType = type;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		final var filters = new ArrayList<Bson>();
		if (this.pattern != null) {

			filters.add(Queries.filterByValueOrRegexp("message", this.pattern));

		}
		if (this.level != null) {

			filters.add(Queries.filterByValueOrRegexp("level", this.level));
		}

		if (this.componentType != null || this.componentPattern != null) {

			filters.add(Filters.ne("componentId", null));
		}

		if (filters.isEmpty()) {

			return null;

		} else if (filters.size() == 1) {

			return filters.get(0);

		} else {

			return Filters.and(filters);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Bson> createPipelineBeforeFacet() {

		final var pipeline = super.createPipelineBeforeFacet();

		final var componentPipeline = new BsonArray();
		componentPipeline
				.add(Aggregates.project(Projections.include("_id", "type", "name", "description")).toBsonDocument());

		final var componentFilters = new ArrayList<Bson>();
		if (this.componentType != null) {

			componentFilters.add(Queries.filterByValueOrRegexp("type", this.componentType));
		}
		if (this.componentPattern != null) {

			componentFilters.add(Filters.or(Queries.filterByValueOrRegexp("name", this.componentPattern),
					Queries.filterByValueOrRegexp("description", this.componentPattern)));
		}

		if (componentFilters.size() == 1) {

			componentPipeline.add(Aggregates.match(componentFilters.get(0)).toBsonDocument());

		} else if (!componentFilters.isEmpty()) {

			componentPipeline.add(Aggregates.match(Filters.and(componentFilters)).toBsonDocument());
		}
		final var lookup = new BsonDocument("$lookup",
				new BsonDocument("from", new BsonString(ComponentEntity.COLLECTION_NAME))
						.append("localField", new BsonString("componentId"))
						.append("foreignField", new BsonString("_id")).append("as", new BsonString("component"))
						.append("pipeline", componentPipeline));

		pipeline.add(lookup);

		final var ifNullParams = new BsonArray();
		ifNullParams.add(new BsonString("$component"));
		ifNullParams.add(new BsonArray());
		pipeline.add(Aggregates
				.project(Projections.fields(Projections.include("_id", "level", "message", "payload", "timestamp"),
						Projections.computed("component",
								new BsonDocument("$first", new BsonDocument("$ifNull", ifNullParams))))));
		if (this.componentType != null || this.componentPattern != null) {

			pipeline.add(Aggregates.match(Filters.ne("component", null)));
		}
		return pipeline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<LogRecordPage> getPageWith(List<Bson> pipeline) {

		return LogEntity.mongoCollection().aggregate(pipeline, LogRecordPage.class).collect().first().onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some logs");
					final var page = new LogRecordPage();
					page.offset = this.offset;
					return page;
				});

	}

}
