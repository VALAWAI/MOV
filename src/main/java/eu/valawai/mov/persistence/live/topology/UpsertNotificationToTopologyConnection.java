/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Update or insert a notification defined in a topology connection.
 *
 * @see TopologyConnectionEntity#notifications
 *
 * @author VALAWAI
 */
public class UpsertNotificationToTopologyConnection
		extends AbstractTopologyConnectionOperation<Boolean, UpsertNotificationToTopologyConnection> {

	/**
	 * The notification to add.
	 */
	protected TopologyConnectionNotification notification;

	/**
	 * Create the operation.
	 */
	private UpsertNotificationToTopologyConnection() {
	}

	/**
	 * Create the operation to add a subscription into a connection.
	 *
	 * @return the new instance of the operation.
	 */
	public static UpsertNotificationToTopologyConnection fresh() {

		return new UpsertNotificationToTopologyConnection();
	}

	/**
	 * Set the notification to add.
	 *
	 * @param notification to add.
	 *
	 * @return this operator.
	 */
	public UpsertNotificationToTopologyConnection witNotification(TopologyConnectionNotification notification) {

		this.notification = notification;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		final var filter = Filters.and(Filters.eq("_id", this.connectionId));
		final List<Bson> updates = Arrays.asList(new Document("$set",
				new Document("notifications",
						new Document("$concatArrays",
								Arrays.asList(
										new Document("$filter", new Document("input",
												new Document("$ifNull",
														Arrays.asList("$notifications", Collections.EMPTY_LIST)))
												.append("as", "notification")
												.append("cond", new Document("$or", Arrays.asList(
														new Document("$ne",
																Arrays.asList("$$notification.node.componentId",
																		this.notification.node.componentId)),
														new Document("$ne",
																Arrays.asList("$$notification.node.channelName",
																		this.notification.node.channelName)))))),
										Arrays.asList(new Document("node",
												new Document("componentId", this.notification.node.componentId)
														.append("channelName", this.notification.node.channelName))
												.append("enabled", this.notification.enabled)
												.append("notificationMessageConverterJSCode",
														this.notification.notificationMessageConverterJSCode)))))
						.append("updateTimestamp", TimeManager.now())));

		return TopologyConnectionEntity.mongoCollection().updateOne(filter, updates).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot add the notification into the connection {0}", this.connectionId);
					return null;

				}).map(result -> result != null && result.getModifiedCount() > 0);

	}

}
