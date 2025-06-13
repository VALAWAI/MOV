/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Finish a component.
 *
 * @see ComponentEntity#finishedTime
 *
 * @author VALAWAI
 */
public class FinishComponent extends AbstractComponentOperation<Boolean, FinishComponent> {

	/**
	 * Create a new operator.
	 */
	private FinishComponent() {
	}

	/**
	 * Create the operator to delete a topology connection.
	 *
	 * @return the operator to delete a connection.
	 */
	public static FinishComponent fresh() {

		return new FinishComponent();
	}

	/**
	 * Mark a component as fnished.
	 *
	 * {@inheritDoc}
	 *
	 * @return {@code true} if the state has been changed.
	 */
	@Override
	public Uni<Boolean> execute() {

		final var filter = Filters.and(Filters.eq("_id", this.componentId),
				Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)));
		final var update = Updates.set("finishedTime", TimeManager.now());
		return ComponentEntity.mongoCollection().updateOne(filter, update).onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot mark the component {0} as finished", this.componentId);
			return null;

		}).map(result -> result != null && result.getMatchedCount() > 0);

	}

}
