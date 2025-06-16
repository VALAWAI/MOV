/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import java.util.regex.Pattern;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import eu.valawai.mov.persistence.live.components.FinishAllComponents;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.DeleteAllTopologyConnections;
import eu.valawai.mov.persistence.live.topology.DisableAllTopologyConnections;
import eu.valawai.mov.services.ComponenetLibraryService;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.config.Priorities;
import io.vertx.mutiny.core.http.HttpHeaders;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

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
	@ConfigProperty(name = MOVSettings.CLEAN_ON_STARTUP, defaultValue = "true")
	protected boolean cleanOnStartup;

	/**
	 * If this is {@code true} it has to update the components library on start up.
	 */
	@ConfigProperty(name = MOVSettings.COMPONENTS_LIBRARY_UPDATE_ON_START, defaultValue = "true")
	protected boolean updateOnStart;

	/**
	 * The data of the last time the component library has been updated.
	 */
	@ConfigProperty(name = MOVSettings.COMPONENTS_LIBRARY_LAST_UPDATE, defaultValue = "0")
	protected long lastUpdate;

	/**
	 * The seconds to wait until update process.
	 */
	@ConfigProperty(name = MOVSettings.COMPONENTS_LIBRARY_UPDATE_PERIOD, defaultValue = "86400")
	protected long updatePeriod;

	/**
	 * The pattern to check the on page resource.
	 */
	private static final Pattern INDEX_PATTERN = Pattern.compile(".*(/[a-z]{2})(/.*)?");

	/**
	 * The name of the context variable that is used to mark that the request is
	 * re-routing.
	 */
	private static final String REROUTING_SOURCE = "re-routing-source";

	/**
	 * The service to update the library.
	 */
	@Inject
	ComponenetLibraryService libraryService;

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

		if (this.updateOnStart && this.lastUpdate + this.updatePeriod < TimeManager.now()) {
			// Needs to update the library
			this.libraryService.update().onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error).withMessage("Cannot start the update of the components library.")
						.store();
				return null;

			}).await().indefinitely();
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
			if (path.endsWith("env.js")) {
				// redirect to the API resource
				rc.reroute("/env.js");

			} else if (rc.get(REROUTING_SOURCE) == null && this.isHtmlRequest(rc)) {

				final var matcher = INDEX_PATTERN.matcher(path);
				var lang = "en";
				if (matcher.find()) {

					final var group = matcher.group();
					lang = group.substring(1, 3);
				}
				// Redirect for one Page Angular
				rc.put(REROUTING_SOURCE, path);
				rc.reroute("/" + lang + "/index.html");

			} else {
				// Must be handled by another
				rc.next();
			}

		});

	}

	/**
	 * Check if the request accept HTML page.
	 *
	 * @param context of the request.
	 *
	 * @return {@code true} if the request accept HTML content.
	 */
	private boolean isHtmlRequest(RoutingContext context) {

		final var accept = context.request().getHeader(HttpHeaders.ACCEPT);
		return accept == null || accept.contains("text/html");
	}

}
