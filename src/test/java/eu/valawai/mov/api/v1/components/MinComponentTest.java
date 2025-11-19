/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.time.Duration;

import org.bson.types.ObjectId;

import eu.valawai.mov.persistence.live.components.ComponentEntity;
import io.smallrye.mutiny.Uni;

/**
 * Test the {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @author VALAWAI
 */
public class MinComponentTest extends AbstractMinComponentTestCase<MinComponent> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MinComponent createEmptyModel() {

		return new MinComponent();
	}

	/**
	 * Return the model from an entity.
	 *
	 * @param entity to get the model.
	 *
	 * @return the model from the entity.
	 */
	public static MinComponent from(ComponentEntity entity) {

		if (entity == null) {

			return null;

		} else {

			final var model = new MinComponent();
			model.id = entity.id;
			model.type = entity.type;
			model.name = entity.name;
			model.description = entity.description;
			return model;

		}
	}

	/**
	 * Return the model from an identifier.
	 *
	 * @param id identifier of the component to get.
	 *
	 * @return the model from the entity.
	 */
	public static MinComponent from(ObjectId id) {

		if (id == null) {

			return null;

		} else {

			final Uni<ComponentEntity> find = ComponentEntity.findById(id);
			final ComponentEntity entity = find.onFailure().recoverWithNull().await().atMost(Duration.ofSeconds(30));
			return from(entity);

		}
	}

}
