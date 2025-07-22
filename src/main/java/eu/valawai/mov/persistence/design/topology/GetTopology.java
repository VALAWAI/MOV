/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;

import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.api.v2.design.topologies.TopologyConnection;
import eu.valawai.mov.api.v2.design.topologies.TopologyNode;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import io.smallrye.mutiny.Uni;

/**
 * Obtain the {@link Topology}.
 *
 * @see Topology
 * @see TopologyGraphEntity
 * @see TopologyNode
 * @see TopologyConnection
 * @see TopologyGraphNode
 * @see TopologyGraphNodeOutputConnection
 *
 * @author VALAWAI
 */
public class GetTopology extends AbstractEntityOperator<Topology, GetTopology> {

	/**
	 * The identifier of the topology to get.
	 */
	protected ObjectId id;

	/**
	 * Create the operator.
	 */
	private GetTopology() {

	}

	/**
	 * Create the operator to get a {@link Topology}.
	 *
	 * @return the operator to get the topology.
	 */
	public static GetTopology fresh() {

		return new GetTopology();

	}

	/**
	 * Specify the identifier of the topology to get.
	 *
	 * @param id identifier of the topology to get.
	 *
	 * @return the operator to get the topology.
	 */
	public GetTopology withId(final ObjectId id) {

		this.id = id;
		return this;
	}

	/**
	 * Get the defined {@link Topology} from the {@link TopologyGraphEntity}.
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Topology> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(Filters.eq("_id", this.id)));

		final var nodesPipeline = new ArrayList<Bson>();
		nodesPipeline.add(Aggregates.project(Projections.include("_id", "nodes")));
		nodesPipeline.add(Aggregates.match(Filters.and(Filters.exists("nodes", true), Filters.ne("nodes", null),
				Filters.not(Filters.size("nodes", 0)))));
		nodesPipeline.add(Aggregates.unwind("$nodes", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		nodesPipeline.add(Aggregates.lookup(ComponentDefinitionEntity.COLLECTION_NAME, "nodes.componentRef", "_id",
				"components"));
		nodesPipeline.add(Aggregates.project(Projections.fields(Projections.computed("tag", "$nodes.tag"),
				Projections.computed("position", new Document("x", "$nodes.x").append("y", "$nodes.y")),
				Projections.computed("component",
						new Document("_id", new Document("$first", "$components._id"))
								.append("type", new Document("$first", "$components.type"))
								.append("name", new Document("$first", "$components.name"))
								.append("description", new Document("$first", "$components.description"))
								.append("docsLink", new Document("$first", "$components.docsLink"))
								.append("gitHubLink", new Document("$first", "$components.repository.html_url"))
								.append("version", new Document("$first", "$components.version"))
								.append("apiVersion", new Document("$first", "$components.apiVersion"))
								.append("channels", new Document("$first", "$components.channels"))
								.append("updatedAt", new Document("$first", "$components.updatedAt"))))));

		final var connectionsPipeline = new ArrayList<Bson>();
		connectionsPipeline.add(Aggregates.project(Projections.include("_id", "nodes")));
		connectionsPipeline.add(Aggregates.match(Filters.and(Filters.exists("nodes", true), Filters.ne("nodes", null),
				Filters.not(Filters.size("nodes", 0)))));
		connectionsPipeline.add(Aggregates.unwind("$nodes", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		connectionsPipeline.add(Aggregates.project(Projections.fields(Projections.computed("sourceTag", "$nodes.tag"),
				Projections.computed("outputs", "$nodes.outputs"))));
		connectionsPipeline.add(Aggregates.match(Filters.and(Filters.exists("outputs", true),
				Filters.ne("outputs", null), Filters.not(Filters.size("outputs", 0)))));
		connectionsPipeline.add(Aggregates.unwind("$outputs", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		connectionsPipeline
				.add(Aggregates
						.project(Projections.fields(
								Projections.computed("source",
										new Document("nodeTag", "$sourceTag").append("channel",
												"$outputs.sourceChannel")),
								Projections.computed("target",
										new Document("nodeTag", "$outputs.targetTag").append("channel",
												"$outputs.targetChannel")),
								Projections.computed("convertCode", "$outputs.convertCode"),
								Projections.computed("type", "$outputs.type"),
								Projections.computed("notificationPosition", Document.parse("""
										{
										    "$cond": {
										        "if": {
										            "$and": [
										                {
										                    "$ne": [
										                        {
										                            "$ifNull": [
										                                "$outputs.notificationX",
										                                null
										                            ]
										                        },
										                        null
										                    ]
										                },
										                {
										                    "$ne": [
										                        {
										                            "$ifNull": [
										                                "$outputs.notificationY",
										                                null
										                            ]
										                        },
										                        null
										                    ]
										                }
										            ]
										        },
										        "then": {
										            "x": "$outputs.notificationX",
										            "y": "$outputs.notificationY"
										        },
										        "else": null
										    }
										}
										""")), Projections.computed("notifications", Document.parse("""
										{
										     "$map":{
										       "input":"$outputs.notifications",
										       "as":"notification",
										       "in": {
										         "target":{
										           "nodeTag":"$$notification.targetTag",
										           "channel":"$$notification.targetChannel"
										         },
										         "convertCode":"$$notification.convertCode"
										       }
										     }
										 }""")))));

		pipeline.add(Aggregates.facet(
				new Facet("main", Aggregates.project(Projections.include("_id", "name", "description", "updatedAt"))),
				new Facet("nodes", nodesPipeline), new Facet("connections", connectionsPipeline)));
		pipeline.add(
				Aggregates.project(Projections.fields(Projections.computed("_id", new Document("$first", "$main._id")),
						Projections.computed("name", new Document("$first", "$main.name")),
						Projections.computed("description", new Document("$first", "$main.description")),
						Projections.computed("updatedAt", new Document("$first", "$main.updatedAt")),
						Projections.include("nodes", "connections"))));

		pipeline.add(Aggregates.match(Filters.ne("_id", null)));

		return TopologyGraphEntity.mongoCollection().aggregate(pipeline, Topology.class).collect().first();

	}

}
