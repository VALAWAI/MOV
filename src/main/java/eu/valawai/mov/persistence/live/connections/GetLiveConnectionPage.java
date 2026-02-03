/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.connections;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v2.live.connections.LiveConnection;
import eu.valawai.mov.api.v2.live.connections.LiveConnectionPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.smallrye.mutiny.Uni;

/**
 * The operation to obtain some {@link LiveConnection}s from the database.
 *
 * @see LiveConnection
 * @see LiveConnectionPage
 *
 * @author VALAWAI
 */
public class GetLiveConnectionPage extends AbstractGetPage<LiveConnectionPage, GetLiveConnectionPage> {

	/**
	 * Create the query.
	 */
	private GetLiveConnectionPage() {

		super("connections");
	}

	/**
	 * Create the query to get some {@link LiveConnection}s.
	 *
	 * @return the query.
	 */
	public static GetLiveConnectionPage fresh() {

		return new GetLiveConnectionPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<LiveConnectionPage> getPageWith(List<Bson> pipeline) {

		return TopologyConnectionEntity.mongoCollection().aggregate(pipeline, LiveConnectionPage.class).collect()
				.first();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		return Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null));
	}

}
