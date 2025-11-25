/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import java.util.regex.Pattern;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import eu.valawai.mov.MOVConfiguration.UpdateMode;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.components.FinishAllComponents;
import eu.valawai.mov.persistence.live.logs.AddLog;
import eu.valawai.mov.persistence.live.topology.DeleteAllTopologyConnections;
import eu.valawai.mov.persistence.live.topology.DisableAllTopologyConnections;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import eu.valawai.mov.services.ComponenetLibraryService;
import io.quarkus.logging.Log;
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
	 * The pattern to check the on page resource.
	 */
	private static final Pattern INDEX_PATTERN = Pattern.compile("^.*(/[a-z]{2})(/.*)?$");

	/**
	 * The name of the context variable that is used to mark that the request is
	 * re-routing.
	 */
	private static final String REROUTING_SOURCE = "re-routing-source";

	/**
	 * The UI root path.
	 */
	@ConfigProperty(name = "quarkus.http.root-path", defaultValue = "/")
	String uiRootPath;

	/**
	 * The configuration of the MOV.
	 */
	@Inject
	MOVConfiguration conf;

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

		this.initComponents();
		this.initConnections();

		final var updateConentsLibraryMode = this.conf.init().updateComponentsLibrary();
		if (updateConentsLibraryMode == UpdateMode.ALWAYS
				|| updateConentsLibraryMode == UpdateMode.IF_STALE && this.conf.componentsLibrary().lastUpdate()
						+ this.conf.componentsLibrary().updatePeriod() < TimeManager.now()) {
			// Needs to update the library
			this.libraryService.update().onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error).withMessage("Cannot start the update of the components library.")
						.store();
				return null;

			}).await().indefinitely();
		}

	}

	/**
	 * Adapt the {@link ComponentEntity} when the MOV start.
	 */
	private void initComponents() {

		final var mode = this.conf.init().components();
		final var action = switch (mode) {
			case FINISH -> FinishAllComponents.fresh().execute()
					.chain(any -> AddLog.fresh().withInfo().withMessage("Finished the previous components").execute());
			case DROP -> ComponentEntity.mongoCollection().drop()
					.chain(any -> AddLog.fresh().withInfo().withMessage("Dropped the previous components").execute());
			default -> AddLog.fresh().withInfo().withMessage("Preserve the previous components").execute();
		};

		action.onFailure().recoverWithUni(
				cause -> AddLog.fresh().withError(cause).withMessage("Could not {0} the components.", mode).execute())
				.await().indefinitely();

	}

	/**
	 * Adapt the {@link TopologyConnectionEntity} when the MOV start.
	 */
	private void initConnections() {

		final var mode = this.conf.init().connections();
		final var action = switch (mode) {
			case DISABLE -> DisableAllTopologyConnections.fresh().execute()
					.chain(any -> AddLog.fresh().withInfo().withMessage("Disable the previous connections").execute());
			case DELETE -> DeleteAllTopologyConnections.fresh().execute()
					.chain(any -> AddLog.fresh().withInfo().withMessage("Delete the previous connections").execute());
			case DROP -> TopologyConnectionEntity.mongoCollection().drop()
					.chain(any -> AddLog.fresh().withInfo().withMessage("Dropped the previous connections").execute());
			default -> AddLog.fresh().withInfo().withMessage("Preserve the previous connections").execute();
		};

		action.onFailure().recoverWithUni(
				cause -> AddLog.fresh().withError(cause).withMessage("Could not {0} the connections.", mode).execute())
				.await().indefinitely();

	}

	/**
	 * Called when the application has been started.
	 *
	 * @param router for the webs.
	 */
	public void init(@Observes Router router) {

		router.get().last().handler(rc -> {

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
				Log.warnv("Rerouting to {0}{1}/index.html", this.uiRootPath, lang);
				rc.reroute(this.uiRootPath + lang + "/index.html");

			} else {
				// Must be handled by another
				Log.warnv("Unexpected routing to {0}", path);
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
