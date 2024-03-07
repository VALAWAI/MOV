/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.events.topology.TopologyAction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Enable or disable a connection from the topology.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class EnableTopologyConnection {

	/**
	 * The identifier of the connection to modify.
	 */
	protected ObjectId connectionId;

	/**
	 * The enable value for the connection.
	 */
	protected boolean enabled = true;

	/**
	 * Create a new operator.
	 */
	private EnableTopologyConnection() {

	}

	/**
	 * Create the operator to enable/disable a topology connection.
	 *
	 * @return the opertor to change the enable status of a connection.
	 */
	public static EnableTopologyConnection fresh() {

		return new EnableTopologyConnection();
	}

	/**
	 * Set the identifier of the connection to change.
	 *
	 * @param connectionId identifier of the connection to change the enable status.
	 *
	 * @return this operator.
	 */
	public EnableTopologyConnection withConnection(ObjectId connectionId) {

		this.connectionId = connectionId;
		return this;
	}

	/**
	 * Set the type of action that change the connection status.
	 *
	 * @param action do do over the connection.
	 *
	 * @return this operator.
	 */
	public EnableTopologyConnection withAction(TopologyAction action) {

		if (action == TopologyAction.ENABLE) {

			this.enabled = true;

		} else {

			this.enabled = false;
		}
		return this;
	}

	/**
	 * Change the state of the connection.
	 *
	 * @return {@code true} if the state has been changed.
	 */
	public Uni<Boolean> execute() {

		final var filter = Filters.and(Filters.eq("_id", this.connectionId),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final var update = Updates.combine(Updates.set("enabled", this.enabled),
				Updates.set("updateTimestamp", TimeManager.now()));
		return TopologyConnectionEntity.mongoCollection().updateOne(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot change the enable status of the connection {0}", this.connectionId);
					return null;

				}).map(result -> result != null && result.getMatchedCount() > 0);

	}

}
