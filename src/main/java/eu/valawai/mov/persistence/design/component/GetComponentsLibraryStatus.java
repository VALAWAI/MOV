/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import eu.valawai.mov.api.v2.design.components.ComponentsLibraryStatus;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.smallrye.mutiny.Uni;

/**
 * Return the {@link ComponentsLibraryStatus}.
 *
 * @see ComponentsLibraryStatus
 * @see ComponentDefinitionEntity
 *
 * @author VALAWAI
 */
public class GetComponentsLibraryStatus
		extends AbstractEntityOperator<ComponentsLibraryStatus, GetComponentsLibraryStatus> {

	/**
	 * Create the operator.
	 */
	private GetComponentsLibraryStatus() {
	}

	/**
	 * Create the operator to get the status.
	 *
	 * @return the operator to use.
	 */
	public static final GetComponentsLibraryStatus fresh() {

		return new GetComponentsLibraryStatus();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return the status of the library.
	 */
	@Override
	public Uni<ComponentsLibraryStatus> execute() {

		final var pipeline = new ArrayList<Bson>();
		final var componentCount = new Facet("componentCount", Aggregates.count());
		final var oldestComponentTimestamp = new Facet("oldestComponentTimestamp",
				Arrays.asList(Aggregates.sort(Sorts.ascending("updatedAt")), Aggregates.limit(1)));
		final var newestComponentTimestamp = new Facet("newestComponentTimestamp",
				Arrays.asList(Aggregates.sort(Sorts.descending("updatedAt")), Aggregates.limit(1)));
		pipeline.add(
				Aggregates.facet(Arrays.asList(componentCount, oldestComponentTimestamp, newestComponentTimestamp)));
		pipeline.add(Aggregates.project(Projections.fields(
				Projections.computed("componentCount", new Document("$first", "$componentCount.count")),
				Projections.computed("oldestComponentTimestamp",
						new Document("$first", "$oldestComponentTimestamp.updatedAt")),
				Projections.computed("newestComponentTimestamp",
						new Document("$first", "$newestComponentTimestamp.updatedAt")))));
		return ComponentDefinitionEntity.mongoCollection().aggregate(pipeline, ComponentsLibraryStatus.class).collect()
				.first();

	}

}
