/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import eu.valawai.mov.api.v1.topology.TopologyConnection;
import eu.valawai.mov.persistence.components.ComponentEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Operator to obtain a {@link TopologyConnection} from the database.
 *
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
public class GetTopologyConnection
		extends AbstractTopologyConnectionOperation<TopologyConnection, GetTopologyConnection> {

	/**
	 * Create the operator.
	 */
	private GetTopologyConnection() {
	}

	/**
	 * Create the operator to obtain the {@link TopologyConnection}.
	 *
	 * @return the operator to obtain the connection.
	 */
	public static GetTopologyConnection fresh() {

		return new GetTopologyConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<TopologyConnection> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(Filters.and(Filters.eq("_id", this.connectionId), Filters
				.or(Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null))))));
		pipeline.add(
				Aggregates.lookup(ComponentEntity.COLLECTION_NAME, "source.componentId", "_id", "source.component"));
		pipeline.add(
				Aggregates.lookup(ComponentEntity.COLLECTION_NAME, "target.componentId", "_id", "target.component"));
		pipeline.add(Aggregates
				.project(Projections.fields(Projections.include("_id", "enabled", "createTimestamp", "updateTimestamp"),
						this.projectTopologyConnectionNode("source"), this.projectTopologyConnectionNode("target"))));

		return TopologyConnectionEntity.mongoCollection().aggregate(pipeline, TopologyConnection.class).collect()
				.first().onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot obtain the topology connection {0}", this.connectionId);
					return null;
				});
	}

	/**
	 * Return the code to project a node.
	 *
	 * @param field name of the filed of the node to project.
	 *
	 * @return the code to project the node filed.
	 */
	private Bson projectTopologyConnectionNode(String field) {

		final var filter = new Document("$filter",
				new Document().append("input", new Document("$first", "$" + field + ".component.channels"))
						.append("as", "item").append("cond",
								new Document("$eq", Arrays.asList("$" + field + ".channelName", "$$item.name"))));
		final var channelProjection = new Document("$first", filter);
		return Projections.computed(field,
				new Document().append("component", new Document("$first", "$" + field + ".component")).append("channel",
						channelProjection));

	}
}
