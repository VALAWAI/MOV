/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.events.topology.TopologyAction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Enable or disable a notification defined in a connection from the topology.
 *
 * @see TopologyConnectionEntity#notifications
 * @see TopologyConnectionNotification#enabled
 *
 * @author VALAWAI
 */
public class ChangeNotificationFromConnection
		extends AbstractNotificationConnectionOperation<Boolean, ChangeNotificationFromConnection> {

	/**
	 * The action to do over the notification of the connection.
	 */
	protected TopologyAction action;

	/**
	 * Create a new operator.
	 */
	private ChangeNotificationFromConnection() {

	}

	/**
	 * Create the operator to enable/disable a topology connection.
	 *
	 * @return the operator to change the enable status of a connection.
	 */
	public static ChangeNotificationFromConnection fresh() {

		return new ChangeNotificationFromConnection();
	}

	/**
	 * Set the action to do over the notification defined in a connection.
	 *
	 * @param action do do over the notification.
	 *
	 * @return this operator.
	 */
	public ChangeNotificationFromConnection withAction(TopologyAction action) {

		this.action = action;
		return this;
	}

	/**
	 * Change the state of the connection.
	 *
	 * @return {@code true} if the state has been changed.
	 */
	@Override
	public Uni<Boolean> execute() {

		final var filter = Filters.and(Filters.eq("_id", this.connectionId),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.eq("notifications.node", this.node));
		var update = Updates.set("updateTimestamp", TimeManager.now());
		if (this.action != TopologyAction.REMOVE) {

			update = Updates.combine(Updates.set("notifications.$.enabled", this.action == TopologyAction.ENABLE),
					update);

		} else {

			update = Updates.combine(Updates.pull("notifications", new Document("node", this.node)), update);
		}
		return TopologyConnectionEntity.mongoCollection().updateOne(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot change the enable status of the notification {0} in the connection {1}",
							this.node, this.connectionId);
					return null;

				}).map(result -> result != null && result.getModifiedCount() > 0);

	}

}
