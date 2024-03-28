/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import eu.valawai.mov.persistence.components.FinishAllComponents;
import eu.valawai.mov.persistence.logs.AddLog;
import eu.valawai.mov.persistence.topology.DeleteAllTopologyConnections;
import eu.valawai.mov.persistence.topology.DisableAllTopologyConnections;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * The class that manage when the application has been started.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class OnStart {

	/**
	 * If this is {@code true} it removes all the previous data on start up.
	 */
	@ConfigProperty(name = "mov.cleanOnStartup", defaultValue = "true")
	protected boolean cleanOnStartup;

	/**
	 * Called when the application has been started.
	 *
	 * @param event that contains the start status.
	 */
	public void handle(@Observes StartupEvent event) {

		if (this.cleanOnStartup) {

			FinishAllComponents.fresh().execute().subscribe().with(success -> {

				AddLog.fresh().withInfo().withMessage("Finished the previous components").store();

			}, error -> {

				AddLog.fresh().withError(error).withMessage("Cannot finish the previous components").store();

			});

			DeleteAllTopologyConnections.fresh().execute().subscribe().with(success -> {

				AddLog.fresh().withInfo().withMessage("Deleted the previous topology connections").store();

			}, error -> {

				AddLog.fresh().withError(error).withMessage("Cannot delete the previous topology connections").store();

			});

		} else {

			DisableAllTopologyConnections.fresh().execute().subscribe().with(success -> {

				AddLog.fresh().withInfo().withMessage("Disabled the previous topology connections").store();

			}, error -> {

				AddLog.fresh().withError(error).withMessage("Cannot disable the previous topology connections").store();

			});
		}

	}
}
