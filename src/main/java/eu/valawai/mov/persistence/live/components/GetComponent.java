/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import java.util.ArrayList;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.Component;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Obtain a defined {@link Component}.
 *
 * @see Component
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public class GetComponent extends AbstractComponentOperation<Component, GetComponent> {

	/**
	 * The operator to get the component.
	 */
	private GetComponent() {
	}

	/**
	 * Create the operator to get the component.
	 *
	 * @return the new operator to get the component.
	 */
	public static GetComponent fresh() {

		return new GetComponent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Component> execute() {

		final var pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.match(Filters.and(Filters.eq("_id", this.componentId),
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)))));
		return ComponentEntity.mongoCollection().aggregate(pipeline, Component.class).collect().first().onFailure()
				.recoverWithItem(error -> {

					Log.errorv(error, "Cannot get the component {0}", this.componentId);
					return null;
				});
	}

}
