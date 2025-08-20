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
import eu.valawai.mov.persistence.live.components.AbstractComponentOperation;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Remove all the notifications that a component is involved.
 *
 * @see TopologyConnectionEntity#notifications
 *
 * @author VALAWAI
 */
public class RemoveAllNotificationsWithComponent
		extends AbstractComponentOperation<Long, RemoveAllNotificationsWithComponent> {

	/**
	 * Create the operator.
	 */
	private RemoveAllNotificationsWithComponent() {
	}

	/**
	 * Create the operator to remove all the subscription references of a component
	 * into all the topology connections.
	 *
	 * @return a new operator to remove the subscription of a component.
	 */
	public static RemoveAllNotificationsWithComponent fresh() {

		return new RemoveAllNotificationsWithComponent();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return the number of modified connections.
	 */
	@Override
	public Uni<Long> execute() {

		final var filter = Filters.and(
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)),
				Filters.eq("notifications.node.componentId", this.componentId));
		final var update = Updates.combine(
				Updates.pull("notifications", new Document("node.componentId", this.componentId)),
				Updates.set("updateTimestamp", TimeManager.now()));
		return TopologyConnectionEntity.mongoCollection().updateMany(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot remove the Notifications of {0}", this.componentId);
					return null;

				}).map(updated -> {

					if (updated == null) {

						return 0l;

					} else {

						return updated.getModifiedCount();
					}
				});
	}

}
