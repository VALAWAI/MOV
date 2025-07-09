/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import java.util.ArrayList;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v2.design.components.ComponentDefinition;
import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import io.smallrye.mutiny.Uni;

/**
 * Get {@link ComponentDefinition} from the data base.
 *
 * @see ComponentDefinition
 *
 * @author VALAWAI
 */
public class GetComponentDefinition extends AbstractEntityOperator<ComponentDefinition, GetComponentDefinition> {

	/**
	 * The identifier of the topology to get.
	 */
	protected ObjectId id;

	/**
	 * Create the operator.
	 */
	private GetComponentDefinition() {

	}

	/**
	 * Create the operator to get a {@link Topology}.
	 *
	 * @return the operator to get the topology.
	 */
	public static GetComponentDefinition fresh() {

		return new GetComponentDefinition();

	}

	/**
	 * Specify the identifier of the topology to get.
	 *
	 * @param id identifier of the topology to get.
	 *
	 * @return the operator to get the topology.
	 */
	public GetComponentDefinition withId(final ObjectId id) {

		this.id = id;
		return this;
	}

	/**
	 * Get the defined {@link Topology} from the {@link TopologyGraphEntity}.
	 * {@inheritDoc}
	 */
	@Override
	public Uni<ComponentDefinition> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(Filters.eq("_id", this.id)));
		pipeline.add(Aggregates.set(new Field<>("gitHubLink", "$repository.html_url")));

		return ComponentDefinitionEntity.mongoCollection().aggregate(pipeline, ComponentDefinition.class).collect()
				.first();

	}

}
