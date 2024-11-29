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
import io.smallrye.config.Priorities;
import io.vertx.mutiny.ext.web.Router;
import jakarta.annotation.Priority;
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
	public void handle(@Observes @Priority(Priorities.APPLICATION + 23) StartupEvent event) {

		if (this.cleanOnStartup) {

			FinishAllComponents.fresh().execute().onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error).withMessage("Cannot finish some previous components").store();
				return null;

			}).await().indefinitely();

			AddLog.fresh().withInfo().withMessage("Finished the previous components").store();

			DeleteAllTopologyConnections.fresh().execute().onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error).withMessage("Cannot delete some previous topology connections").store();
				return null;

			}).await().indefinitely();

			AddLog.fresh().withInfo().withMessage("Deleted the previous topology connections").store();

		} else {

			DisableAllTopologyConnections.fresh().execute().onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error).withMessage("Cannot disable some previous topology connections")
						.store();
				return null;

			}).await().indefinitely();

			AddLog.fresh().withInfo().withMessage("Disabled the previous topology connections").store();
		}

	}

	/**
	 * Called when the application has been started.
	 *
	 * @param router for the webs.
	 */
	public void init(@Observes Router router) {

		router.getWithRegex("/.*").last().handler(rc -> {

			final var path = rc.normalizedPath();
			if (!path.matches("/([ca|es|en]/)?index.html")) {

				rc.fail(404);

			} else {

				var lang = "en";
				if (path.matches("/([ca|es|en]/).*")) {

					lang = path.substring(1, 3);
				}
				rc.reroute("/" + lang + "/index.html");
			}

		});
	}

}
