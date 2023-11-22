/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import static eu.valawai.mov.persistence.Repositories.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentPage;
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
	 * Add a component.
	 *
	 * @param component to add.
	 *
	 * @return {@code true} if the component has been added.
	 */
	public boolean add(Component component) {

		if (component != null) {

			synchronized (COMPONENTS) {

				component.since = TimeManager.now();
				final var max = COMPONENTS.stream().max((c1, c2) -> c1.id.compareTo(c2.id));
				if (max.isPresent()) {

					final var newId = Integer.parseInt(max.get().id) + 1;
					component.id = String.valueOf(newId);

				} else {

					component.id = "1";
				}
				COMPONENTS.add(component);
			}

			return true;

		} else {

			return false;
		}

	}

	/**
	 * Count the number of components that contains the repository
	 *
	 * @return the number of components that are stored on the repository.
	 */
	public int count() {

		synchronized (COMPONENTS) {

			return COMPONENTS.size();
		}

	}

	/**
	 * Clear all the components.
	 */
	public void clear() {

		synchronized (COMPONENTS) {

			COMPONENTS.clear();
		}
	}

	/**
	 * Obtain the last component record.
	 *
	 * @return the last record message or {@code null} if not record exists.
	 */
	public Component last() {

		synchronized (COMPONENTS) {

			final var index = COMPONENTS.size() - 1;
			if (index > -1) {

				return COMPONENTS.get(index);

			} else {

				return null;
			}
		}
	}

	/**
	 * Return the page with the logs that satisfy the parameters.
	 *
	 * @param pattern to match the logs message or payload.
	 * @param order   to return the logs.
	 * @param offset  to the first log to return.
	 * @param limit   number maximum of logs to return.
	 *
	 * @return the page with the logs that satisfy the parameters.
	 */
	public ComponentPage getComponentPage(String pattern, String order, int offset, int limit) {

		final var page = new ComponentPage();
		page.offset = offset;
		page.components = new ArrayList<>();
		synchronized (COMPONENTS) {

			for (final var component : COMPONENTS) {

				final var add = match(pattern, component.name);
				if (add) {

					page.components.add(component);
				}

			}
		}
		page.total = page.components.size();
		if (order != null) {

			final var factors = order.split("\\s*,\\s*");
			page.components.sort((component1, component2) -> {

				var cmp = 0;
				for (final var factor : factors) {

					switch (factor) {
					case "since":
					case "+since":
						cmp = Long.compare(component1.since, component2.since);
						break;
					case "-since":
						cmp = Long.compare(component2.since, component1.since);
						break;
					case "name":
					case "+name":
						cmp = component1.name.compareTo(component2.name);
						break;
					case "-name":
						cmp = component2.name.compareTo(component1.name);
						break;
					case "type":
					case "+type":
						cmp = component1.type.compareTo(component2.type);
						break;
					case "-type":
						cmp = component2.type.compareTo(component1.type);
						break;
					}
					if (cmp != 0) {
						break;
					}
				}
				return cmp;
			});
		}

		final var max = page.components.size();
		if (offset >= max) {

			page.components = null;

		} else {

			page.components = page.components.subList(offset, Math.min(offset + limit, max));

		}
		return page;

	}

}
