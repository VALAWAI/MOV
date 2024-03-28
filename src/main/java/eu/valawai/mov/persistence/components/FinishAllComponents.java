/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import eu.valawai.mov.TimeManager;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * Mark all the components as finished.
 *
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public class FinishAllComponents {

	/**
	 * Create the operator.
	 */
	private FinishAllComponents() {
	}

	/**
	 * Create the operator to mark as finished all the components.
	 *
	 * @return the new operator.
	 */
	public static FinishAllComponents fresh() {

		return new FinishAllComponents();
	}

	/**
	 * Mark as finished the non finished components.
	 *
	 * @return if the finished is a success.
	 */
	public Uni<Void> execute() {

		final var filter = Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null));
		final var update = Updates.set("finishedTime", TimeManager.now());
		return ComponentEntity.mongoCollection().updateMany(filter, update).map(updated -> {

			Log.debugv("Finished {0} components", updated.getModifiedCount());
			return null;
		});
	}

}
