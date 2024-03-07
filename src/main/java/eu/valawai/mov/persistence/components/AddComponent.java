/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.Component;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Add a component into the data base.
 *
 * @see ComponentEntity
 * @see Component
 *
 * @author VALAWAI
 */
public class AddComponent {

	/**
	 * The component to add.
	 */
	protected Component component;

	/**
	 * Add a new component.
	 */
	private AddComponent() {
	}

	/**
	 * Create a new instance of this operator.
	 *
	 * @return the instance to add a component.
	 */
	public static AddComponent fresh() {

		return new AddComponent();
	}

	/**
	 * Set the component to add.
	 *
	 * @param component to add.
	 *
	 * @return this operation.
	 */
	public AddComponent withComponent(Component component) {

		this.component = component;
		return this;
	}

	/**
	 * Store the component in the database.
	 *
	 * @return the added entitye or {@code null} otherwise.
	 */
	public Uni<ComponentEntity> execute() {

		final var entity = new ComponentEntity();
		entity.name = this.component.name;
		entity.description = this.component.description;
		entity.version = this.component.version;
		entity.apiVersion = this.component.apiVersion;
		entity.type = this.component.type;
		entity.since = TimeManager.now();
		entity.channels = this.component.channels;
		return entity.persist().onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot add the {0}", this.component);
			return null;

		}).map(result -> {

			if (result != null) {

				return entity;

			} else {

				return null;
			}

		});
	}

}
