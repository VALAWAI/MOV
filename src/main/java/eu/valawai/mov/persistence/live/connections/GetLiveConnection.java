/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.connections;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UnwindOptions;

import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.api.v2.live.connections.LiveConnection;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a {@link LiveConnection} from the database.
 *
 * @see LiveConnection
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public class GetLiveConnection extends AbstractEntityOperator<LiveConnection, GetLiveConnection> {

	/**
	 * The identifier of the live connection to get.
	 */
	protected ObjectId id;

	/**
	 * Create the operator.
	 */
	private GetLiveConnection() {

	}

	/**
	 * Create the operator to get a {@link Topology}.
	 *
	 * @return the operator to get the topology.
	 */
	public static GetLiveConnection fresh() {

		return new GetLiveConnection();

	}

	/**
	 * Specify the identifier of the connection to get.
	 *
	 * @param id identifier of the live connection to get.
	 *
	 * @return the operator to get the live connection.
	 */
	public GetLiveConnection withConnection(final ObjectId id) {

		this.id = id;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<LiveConnection> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(Filters.eq("_id", this.id)));
		pipeline.add(Aggregates.unwind("$notifications", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		pipeline.add(Aggregates.lookup(ComponentEntity.COLLECTION_NAME, "notifications.node.componentId", "_id",
				"notificationComponent"));
		pipeline.add(Document.parse("""
				{
				    "$group": {
				      "_id": "$_id",
				      "enabled": { "$first": "$enabled" },
				      "createTimestamp": {
				        "$first": "$createTimestamp"
				      },
				      "updateTimestamp": {
				        "$first": "$updateTimestamp"
				      },
				      "convertCode": {
				        "$first": "$targetMessageConverterJSCode"
				      },
				      "source": { "$first": "$source" },
				      "target": { "$first": "$target" },
				      "notifications": {
				        "$push": {
				          "$cond": {
				            "if": {
				                "$ne":[
				                {
				                	"$size":
				                	{
				                  		"$ifNull":
				                  		[
				                    		"$notificationComponent",
				                    		[]
				                  		]
				                	}
				                },
				                0
				              ]
				            },
				            "then": {
				              "node": {
				                "component": {
				                  "_id": { "$first":"$notificationComponent._id"},
				                  "name": { "$first":"$notificationComponent.name"},
				                  "description": { "$first":"$notificationComponent.description"},
				                  "type": { "$first":"$notificationComponent.type"}
				                },
				                "channelName": "$notifications.node.channelName"
				              },
				              "enabled": "$notifications.enabled",
				              "convertCode": "$notifications.notificationMessageConverterJSCode"
				            },
				            "else": "$$REMOVE"
				          }
				        }
				      }
				    }
				}
				"""));

		pipeline.add(
				Aggregates.lookup(ComponentEntity.COLLECTION_NAME, "source.componentId", "_id", "sourceComponent"));
		pipeline.add(
				Aggregates.lookup(ComponentEntity.COLLECTION_NAME, "target.componentId", "_id", "targetComponent"));

		pipeline.add(Document.parse("""
				{
				    "$project": {
				      "_id": 1,
				      "enabled": 1,
				      "createTimestamp": 1,
				      "updateTimestamp": 1,
				      "notifications": 1,
				      "convertCode": 1,
				      "source": {
				        "component": {
				          "_id": { "$first": "$sourceComponent._id" },
				          "name": {
				            "$first": "$sourceComponent.name"
				          },
				          "description": {
				            "$first": "$sourceComponent.description"
				          },
				          "type": {
				            "$first": "$sourceComponent.type"
				          }
				        },
				        "channelName": "$source.channelName"
				      },
				      "target": {
				        "component": {
				          "_id": { "$first": "$targetComponent._id" },
				          "name": {
				            "$first": "$targetComponent.name"
				          },
				          "description": {
				            "$first": "$targetComponent.description"
				          },
				          "type": {
				            "$first": "$targetComponent.type"
				          }
				        },
				        "channelName": "$target.channelName"
				      }
				    }
				  }
				  """));

		return TopologyConnectionEntity.mongoCollection().aggregate(pipeline, LiveConnection.class).collect().first();

	}

}
