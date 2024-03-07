/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.logs.LogRecordPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
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
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		if (this.pattern != null && this.level != null) {

			return Filters.and(Queries.filterByValueOrRegexp("message", this.pattern),
					Queries.filterByValueOrRegexp("level", this.level));

		} else if (this.pattern != null) {

			return Queries.filterByValueOrRegexp("message", this.pattern);

		} else if (this.level != null) {

			return Queries.filterByValueOrRegexp("level", this.level);

		} else {

			return null;
		}
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
