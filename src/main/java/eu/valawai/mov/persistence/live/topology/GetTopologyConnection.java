/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import eu.valawai.mov.api.v1.topology.TopologyConnection;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Operator to obtain a {@link TopologyConnection} from the database.
 *
 * @see TopologyConnection
 * @see TopologyConnectionEntity
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
		final var subscriptionsPipeline = this.pipelineForTopologyConnectionNode("c2Subscriptions");
		subscriptionsPipeline.add(0, Aggregates.unwind("$c2Subscriptions"));
		pipeline.add(
				Aggregates.facet(
						new Facet("basic",
								Arrays.asList(Aggregates.project(
										Projections.include("_id", "enabled", "createTimestamp", "updateTimestamp")))),
						new Facet("source", this.pipelineForTopologyConnectionNode("source")),
						new Facet("target", this.pipelineForTopologyConnectionNode("target")),
						new Facet("subscriptions", subscriptionsPipeline))

		);
		pipeline.add(Aggregates
				.match(Filters.and(Filters.size("basic", 1), Filters.size("source", 1), Filters.size("target", 1))));
		pipeline.add(Aggregates.project(Document.parse("""
				{
				    "_id": { "$first": "$basic._id" },
				    "enabled": { "$first": "$basic.enabled" },
				    "createTimestamp": { "$first": "$basic.createTimestamp" },
				    "updateTimestamp": { "$first": "$basic.updateTimestamp" },
				    "source": { "$first": "$source" },
				    "target": { "$first": "$target" },
				    "subscriptions":
				    {
				    	"$cond":
				    	{
				    		"if": {"$eq":[{"$size":"$subscriptions"},0]},
				    		"then": null,
				    		"else": "$subscriptions"
				    	}
				    }
				}""")));

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
	private List<Bson> pipelineForTopologyConnectionNode(String field) {

		final List<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.lookup(ComponentEntity.COLLECTION_NAME, field + ".componentId", "_id", "component"));
		pipeline.add(Aggregates.project(Projections.fields(
				Projections.computed("component", new Document("$first", "$component")), Projections.include(field))));
		pipeline.add(Aggregates.unwind("$component.channels"));
		pipeline.add(Aggregates.match(
				Document.parse("{\"$expr\":{\"$eq\":[\"$" + field + ".channelName\",\"$component.channels.name\"]}}")));
		pipeline.add(Aggregates.project(Document.parse("""
				{
				    "component": {
				        "_id": 1,
				        "type": 1,
				        "name": 1,
				        "description": 1
				    },
				    "channel": "$component.channels"
				}""")));
		return pipeline;
	}

}
