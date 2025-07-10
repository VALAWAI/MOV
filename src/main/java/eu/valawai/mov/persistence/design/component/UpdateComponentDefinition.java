/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v2.design.components.ComponentDefinition;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.smallrye.mutiny.Uni;

/**
 * Update the {@link ComponentDefinition}.
 *
 * @see ComponentDefinition
 * @see ComponentDefinitionEntity
 *
 * @author VALAWAI
 */
public class UpdateComponentDefinition extends AbstractEntityOperator<Boolean, UpdateComponentDefinition> {

	/**
	 * The identifier of the component to update.
	 */
	protected ObjectId id;

	/**
	 * The data with the data to update the component.
	 */
	protected ComponentDefinition component;

	/**
	 * Create the operator.
	 */
	private UpdateComponentDefinition() {

	}

	/**
	 * Create the operator to update a {@link ComponentDefinition}.
	 *
	 * @return the operator to get the component.
	 */
	public static UpdateComponentDefinition fresh() {

		return new UpdateComponentDefinition();

	}

	/**
	 * Specify the identifier of the component to get.
	 *
	 * @param id identifier of the component to get.
	 *
	 * @return the operator to get the component.
	 */
	public UpdateComponentDefinition withId(final ObjectId id) {

		this.id = id;
		return this;
	}

	/**
	 * Specify the component data to update the {@link ComponentDefinitionEntity}.
	 *
	 * @param component to update the entity.
	 *
	 * @return the operator to update the component.
	 */
	public UpdateComponentDefinition withComponentDefinition(final ComponentDefinition component) {

		this.component = component;
		return this;
	}

	/**
	 * Update a {@link ComponentDefinitionEntity} with the data of a
	 * {@link ComponentDefinition}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Uni<Boolean> execute() {

		this.component.id = this.id;
		final var filter = Filters.eq("_id", this.id);
		final var update = Updates.combine(Updates.set("type", this.component.type),
				Updates.set("name", this.component.name), Updates.set("description", this.component.description),
				Updates.set("docsLink", this.component.docsLink),
				Updates.set("repository.html_url", this.component.gitHubLink),
				Updates.set("version", this.component.version), Updates.set("apiVersion", this.component.apiVersion),
				Updates.set("channels", this.component.channels), Updates.set("updatedAt", TimeManager.now()));

		return ComponentDefinitionEntity.mongoCollection().updateOne(filter, update)
				.map(updated -> updated != null && updated.getModifiedCount() > 0);

	}

}
