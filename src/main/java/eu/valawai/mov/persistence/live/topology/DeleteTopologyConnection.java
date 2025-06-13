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
 * Mark a {@link TopologyConnectionEntity} as deleted.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class DeleteTopologyConnection extends AbstractTopologyConnectionOperation<Boolean, DeleteTopologyConnection> {

	/**
	 * Create a new operator.
	 */
	private DeleteTopologyConnection() {
	}

	/**
	 * Create the operator to delete a topology connection.
	 *
	 * @return the operator to delete a connection.
	 */
	public static DeleteTopologyConnection fresh() {

		return new DeleteTopologyConnection();
	}

	/**
	 * Change the state of the connection.
	 *
	 * @return {@code true} if the state has been changed.
	 */
	@Override
	public Uni<Boolean> execute() {

		final var filter = Filters.and(Filters.eq("_id", this.connectionId),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final var update = Updates.set("deletedTimestamp", TimeManager.now());
		return TopologyConnectionEntity.mongoCollection().updateOne(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot change the enable status of the connection {0}", this.connectionId);
					return null;

				}).map(result -> result != null && result.getMatchedCount() > 0);

	}

}
