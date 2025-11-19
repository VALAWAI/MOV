/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.connections;

import java.util.ArrayList;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.api.v2.live.connections.LiveConnection;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a {@link LiveConnection} from the database.
 *
 * @see LiveConnection
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

		return ComponentDefinitionEntity.mongoCollection().aggregate(pipeline, LiveConnection.class).collect().first();

	}

}
