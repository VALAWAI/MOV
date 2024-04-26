/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import org.bson.Document;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.components.AbstractComponentOperation;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Remove all the C2 subscription that a component is involved.
 *
 * @see TopologyConnectionEntity#c2Subscriptions
 *
 * @author VALAWAI
 */
public class RemoveAllC2SubscriptionByComponent
		extends AbstractComponentOperation<Long, RemoveAllC2SubscriptionByComponent> {

	/**
	 * Create the operator.
	 */
	private RemoveAllC2SubscriptionByComponent() {
	}

	/**
	 * Create the operator to remove all the subscription references of a component
	 * into all the topology connections.
	 *
	 * @return a new operator to remove the subscription of a component.
	 */
	public static RemoveAllC2SubscriptionByComponent fresh() {

		return new RemoveAllC2SubscriptionByComponent();
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
				Filters.eq("c2Subscriptions.componentId", this.componentId));
		final var update = Updates.combine(
				Updates.pull("c2Subscriptions", new Document("componentId", this.componentId)),
				Updates.set("updateTimestamp", TimeManager.now()));
		return TopologyConnectionEntity.mongoCollection().updateMany(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot remove the C2 subscription of {0}", this.componentId);
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
