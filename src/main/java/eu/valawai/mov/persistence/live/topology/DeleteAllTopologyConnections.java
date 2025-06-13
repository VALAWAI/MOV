/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Mark all the connections as deleted.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class DeleteAllTopologyConnections {

	/**
	 * Create the operator.
	 */
	private DeleteAllTopologyConnections() {
	}

	/**
	 * Create the operator to mark as deleted all the connections.
	 *
	 * @return the new operator.
	 */
	public static DeleteAllTopologyConnections fresh() {

		return new DeleteAllTopologyConnections();
	}

	/**
	 * Mark as deleted the non deleted connections.
	 *
	 * @return if the finished is a success.
	 */
	public Uni<Void> execute() {

		final var filter = Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null));
		final var update = Updates.set("deletedTimestamp", TimeManager.now());
		return TopologyConnectionEntity.mongoCollection().updateMany(filter, update).map(updated -> {

			Log.debugv("Deleted {0} topology connections", updated.getModifiedCount());
			return null;
		});
	}

}
