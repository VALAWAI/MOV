/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Mark all the connections as disabled.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class DisableAllTopologyConnections {

	/**
	 * Create the operator.
	 */
	private DisableAllTopologyConnections() {
	}

	/**
	 * Create the operator to mark as disabled all the connections.
	 *
	 * @return the new operator.
	 */
	public static DisableAllTopologyConnections fresh() {

		return new DisableAllTopologyConnections();
	}

	/**
	 * Mark as deleted the non disabled connections.
	 *
	 * @return if the finished is a success.
	 */
	public Uni<Void> execute() {

		final var filter = Filters.and(Filters.eq("enabled", true),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final var update = Updates.combine(Updates.set("enabled", false),
				Updates.set("updateTimestamp", TimeManager.now()));
		return TopologyConnectionEntity.mongoCollection().updateMany(filter, update).map(updated -> {

			Log.debugv("Disabled {0} topology connections", updated.getModifiedCount());
			return null;
		});
	}

}
