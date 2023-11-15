/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.valawai.mov.api.v1.components.Component;
import jakarta.inject.Singleton;

/**
 * The repository that provides {@link Component}.
 *
 * @see Component
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Singleton
public class ComponentRepository {

	/**
	 * The defined components.
	 */
	private static volatile List<Component> COMPONENTS = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Return the first component that has the specified name.
	 *
	 * @param name for the component.
	 *
	 * @return the first component with the specified name or {@code null} if cannot
	 *         found any.
	 */
	public Component firstByName(String name) {

		if (name != null) {

			synchronized (COMPONENTS) {

				for (final var component : COMPONENTS) {

					if (name.equals(component.name)) {

						return component;
					}

				}

			}
		}

		return null;

	}

	/**
	 * Add a component.
	 *
	 * @param component to add.
	 *
	 * @return {@code true} if the component has been added.
	 */
	public boolean add(Component component) {

		if (component != null) {

			synchronized (COMPONENTS) {

				return COMPONENTS.add(component);

			}

		} else {

			return false;
		}

	}

}
