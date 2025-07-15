/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;

import eu.valawai.mov.api.v2.live.topologies.LiveTopology;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.Bsons;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.smallrye.mutiny.Uni;

/**
 * Obtain the {@link LiveTopology} define din the database.
 *
 * @see LiveTopology
 * @see ComponentEntity
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class GetLiveTopology extends AbstractEntityOperator<LiveTopology, GetLiveTopology> {

	/**
	 * Create the operation.
	 */
	private GetLiveTopology() {
	}

	/**
	 * Return an instance of the operation to obtain the live topology.
	 *
	 * @return the operation to get the {@link LiveTopology}.
	 */
	public static GetLiveTopology fresh() {

		return new GetLiveTopology();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<LiveTopology> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(
				Aggregates.match(Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null))));
		pipeline.add(Aggregates.project(Projections.include("_id", "type", "name", "description")));
		pipeline.add(Aggregates.lookup(TopologyConnectionEntity.COLLECTION_NAME,
				Arrays.asList(new Variable<>("componentId", "$_id")),
				Arrays.asList(
						Aggregates.match(Filters.and(Document.parse("""
								      {
								        "$expr": [
								          {
								            "$eq": [
								              "$source.componentId",
								              "$$componentId"
								            ]
								          }
								        ]
								      }
								"""),
								Filters.or(Filters.exists("deletedTimestamp", false),
										Filters.eq("deletedTimestamp", null)))),
						Aggregates.project(Projections.fields(Projections.computed("channel", "$source.channelName"),
								Projections.computed("target", new Document("_id", "$target.componentId")
										.append("channel", "$target.channelName").append("enabled", "$enabled")),
								Projections.computed("notifications", Document.parse("""
										    {
										    	"$map":
										    	{
										    		"input": "$c2Subscriptions",
										    		"as": "subscription",
										    		"in":
										    		{
											    		"_id": "$$subscription.componentId",
											    		"channel": "$$subscription.channelName",
											    		"enabled": true
										    		}
										    	}
										    }
										""")))),
						Aggregates.project(Projections.fields(Projections.include("_id", "channel", "target"),
								Projections.computed("notifications", Document.parse("""
										    {
										    	"$cond":
										    	{
										    		"if": {"$eq":[{"$size":{"$ifNull":["$notifications",[]]}},0]},
										    		"then": null,
										    		"else": "$notifications"
										    	}
										    }
										"""))))

				), "connections"));
		pipeline.add(Aggregates.project(Projections.fields(Projections.include("_id", "type", "name", "description"),
				Projections.computed("connections", Document.parse("""
						    {
						    	"$cond":
						    	{
						    		"if": {"$eq":[{"$size":{"$ifNull":["$connections",[]]}},0]},
						    		"then": null,
						    		"else": "$connections"
						    	}
						    }
						""")))));
		pipeline.add(Aggregates.group(null, Accumulators.push("components", "$$ROOT")));
		pipeline.add(Aggregates.project(Projections.computed("components", Document.parse("""
				    {
				    	"$cond":
				    	{
				    		"if": {"$eq":[{"$size":{"$ifNull":["$components",[]]}},0]},
				    		"then": null,
				    		"else": "$components"
				    	}
				    }
				"""))));
		System.err.println(Bsons.toString(pipeline));
		return ComponentEntity.mongoCollection().aggregate(pipeline, LiveTopology.class).collect().first().onItem()
				.ifNull().continueWith(() -> new LiveTopology());
	}

}
