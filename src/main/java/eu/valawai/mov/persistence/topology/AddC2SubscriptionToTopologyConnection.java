/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Add a new subscription to a topology connection.
 *
 * @see TopologyConnectionEntity#c2Subscriptions
 *
 * @author VALAWAI
 */
public class AddC2SubscriptionToTopologyConnection
		extends AbstractTopologyConnectionOperation<Boolean, AddC2SubscriptionToTopologyConnection> {

	/**
	 * The identifier of the component that has to be subscribed.
	 */
	protected ObjectId componentId;

	/**
	 * The name of the channel to be subscribed.
	 */
	protected String channelName;

	/**
	 * Create the operation.
	 */
	private AddC2SubscriptionToTopologyConnection() {
	}

	/**
	 * Create the operation to add a subscription into a connection.
	 *
	 * @return the new instance of the operation.
	 */
	public static AddC2SubscriptionToTopologyConnection fresh() {

		return new AddC2SubscriptionToTopologyConnection();
	}

	/**
	 * Set the identifier of the component to be subscribed.
	 *
	 * @param componentId identifier of the component to be subscribed.
	 *
	 * @return this operator.
	 */
	public AddC2SubscriptionToTopologyConnection withComponent(ObjectId componentId) {

		this.componentId = componentId;
		return this.operator();
	}

	/**
	 * Set the name of the channel to be subscribed.
	 *
	 * @param channelName name of the channel to be subscribed.
	 *
	 * @return this operator.
	 */
	public AddC2SubscriptionToTopologyConnection withChannel(String channelName) {

		this.channelName = channelName;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		final var node = new Document("channelName", this.channelName).append("componentId", this.componentId);
		final var filter = Filters.and(Filters.eq("_id", this.connectionId), Filters.ne("c2Subscriptions", node));
		final var update = Updates.combine(Updates.addToSet("c2Subscriptions", node),
				Updates.set("updateTimestamp", TimeManager.now()));
		return TopologyConnectionEntity.mongoCollection().updateOne(filter, update).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot add C2 subscription into the connection {0}", this.connectionId);
					return null;

				}).map(result -> result != null && result.getModifiedCount() > 0);

	}

}
