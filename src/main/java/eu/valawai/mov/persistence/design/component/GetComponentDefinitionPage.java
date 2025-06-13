/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v2.design.components.ComponentDefinitionPage;
import eu.valawai.mov.persistence.AbstractGetPage;
import eu.valawai.mov.persistence.Queries;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.validation.constraints.NotNull;

/**
 * Get {@link ComponentDefinitionPage} from the data base.
 *
 * @see ComponentDefinitionPage
 *
 * @author VALAWAI
 */
public class GetComponentDefinitionPage extends AbstractGetPage<ComponentDefinitionPage, GetComponentDefinitionPage> {

	/**
	 * The type to match the components to be returned.
	 */
	protected String type;

	/**
	 * Create a new operation.
	 */
	private GetComponentDefinitionPage() {

		super("components");
	}

	/**
	 * Create the operation to obtain some components.
	 *
	 * @return the new get page operation.
	 */
	public static GetComponentDefinitionPage fresh() {

		return new GetComponentDefinitionPage();
	}

	/**
	 * The type to match the components.
	 *
	 * @param type to match the components to return.
	 *
	 * @return this operator.
	 */
	public GetComponentDefinitionPage withType(@NotNull final ComponentType type) {

		return this.withType(type.name());
	}

	/**
	 * The type to match the components.
	 *
	 * @param type to match the components to return.
	 *
	 * @return this operator.
	 */
	public GetComponentDefinitionPage withType(final String type) {

		this.type = type;
		return this.operator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bson createFilter() {

		final var filters = new ArrayList<Bson>();
		if (this.pattern != null) {

			filters.add(Filters.or(Queries.filterByValueOrRegexp("name", this.pattern),
					Queries.filterByValueOrRegexp("description", this.pattern)));

		}
		if (this.type != null) {

			filters.add(Queries.filterByValueOrRegexp("type", this.type));
		}
		if (filters.isEmpty()) {

			return null;

		} else {

			return Filters.and(filters);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Uni<ComponentDefinitionPage> getPageWith(List<Bson> pipeline) {

		return ComponentDefinitionEntity.mongoCollection().aggregate(pipeline, ComponentDefinitionPage.class).collect()
				.first().onFailure().recoverWithItem(error -> {

					Log.errorv(error, "Cannot get some component definitions");
					final var page = new ComponentDefinitionPage();
					page.offset = this.offset;
					return page;
				});

	}

}
