/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import eu.valawai.mov.api.v1.components.Component;
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
	 * @return a {@code true} item if the component has been stored.
	 */
	public Uni<Boolean> execute() {

		return null;
	}

}
