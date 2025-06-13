/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Add a new connection into the topology.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class AddTopologyConnection extends AbstractEntityOperator<ObjectId, AddTopologyConnection> {

	/**
	 * The node that is the source of the connection.
	 */
	protected TopologyNode source;

	/**
	 * The node that is the target of the connection.
	 */
	protected TopologyNode target;

	/**
	 * Create the operator with the default values.
	 */
	private AddTopologyConnection() {

		this.source = new TopologyNode();
		this.target = new TopologyNode();
	}

	/**
	 * Create a new instance of the operation to add a topology connection.
	 *
	 * @return the operation to add a topology connection.
	 */
	public static AddTopologyConnection fresh() {

		return new AddTopologyConnection();
	}

	/**
	 * Specify the source component.
	 *
	 * @param sourceId identifier component of the connection.
	 *
	 * @return this operator.
	 */
	public AddTopologyConnection withSourceComponent(ObjectId sourceId) {

		this.source.componentId = sourceId;
		return this;
	}

	/**
	 * Specify the source channel.
	 *
	 * @param name of channel of the connection.
	 *
	 * @return this operator.
	 */
	public AddTopologyConnection withSourceChannel(String name) {

		this.source.channelName = name;
		return this;
	}

	/**
	 * Specify the target component.
	 *
	 * @param targetId identifier component of the connection.
	 *
	 * @return this operator.
	 */
	public AddTopologyConnection withTargetComponent(ObjectId targetId) {

		this.target.componentId = targetId;
		return this;
	}

	/**
	 * Specify the target channel.
	 *
	 * @param name of channel of the connection.
	 *
	 * @return this operator.
	 */
	public AddTopologyConnection withTargetChannel(String name) {

		this.target.channelName = name;
		return this;
	}

	/**
	 * Add the connection.
	 *
	 * @return the identifier of the added connection or {@code null} if the
	 *         connection has not been added.
	 */
	@Override
	public Uni<ObjectId> execute() {

		final var filter = Filters.and(Filters.eq("source.componentId", this.source.componentId),
				Filters.eq("source.channelName", this.source.channelName),
				Filters.eq("target.componentId", this.target.componentId),
				Filters.eq("target.channelName", this.target.channelName),
				Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)));
		final var now = TimeManager.now();
		final var update = Updates.setOnInsert(new Document().append("createTimestamp", now)
				.append("updateTimestamp", now).append("source", this.toDocument(this.source))
				.append("target", this.toDocument(this.target)).append("enabled", false));
		final var options = new UpdateOptions();
		options.upsert(true);
		return TopologyConnectionEntity.mongoCollection().updateOne(filter, update, options).onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot add a topology connection");
					return null;

				}).map(result -> {

					if (result == null) {

						return null;

					} else {

						final var value = result.getUpsertedId();
						if (value == null || !value.isObjectId()) {

							return null;

						} else {

							return value.asObjectId().getValue();
						}
					}

				});
	}

	/**
	 * Return a document with the data of a node.
	 *
	 * @param node to convert to a document.
	 *
	 * @return the document to set the node.
	 */
	private Document toDocument(TopologyNode node) {

		return new Document().append("componentId", node.componentId).append("channelName", node.channelName);

	}

}
