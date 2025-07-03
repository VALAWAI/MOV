/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import eu.valawai.mov.MOVSettings;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentBuilder;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v2.design.components.VersionInfo;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import eu.valawai.mov.persistence.live.logs.AddLog;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The service to manage the components of the library.
 *
 * @see ComponentDefinitionEntity
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ComponenetLibraryService {

	/**
	 * The component to execute background tasks
	 */
	@Inject
	ManagedExecutor executor;

	/**
	 * The service to get the VALAWAI repositories from GitHub.
	 */
	@RestClient
	GitHubAPIService apiService;

	/**
	 * The service to get the files content of a VALAWAI repository from GitHub.
	 */
	@RestClient
	GitHubRawService rawService;

	/**
	 * The service to store the local configuration of the MOV.
	 */
	@Inject
	LocalConfigService configService;

	/**
	 * Called when has to start the update process.
	 *
	 * @return the result of the start update process.
	 */
	public Uni<Void> update() {

		return Uni.createFrom().item(() -> {

			this.udateInBackground(Collections.emptyList(), 0);
			return null;
		});

	}

	/**
	 * Called when has to update the components is repositories.
	 *
	 * @param repositories repositories of the components to update.
	 * @param pageOffset   the index of the current page.
	 */
	private void udateInBackground(List<GitHubRepository> repositories, int pageOffset) {

		try {

			this.executor.execute(() -> this.udateComponents(repositories, pageOffset));

		} catch (final Throwable cause) {

			AddLog.fresh().withError(cause).withMessage("Cannot update the components library").store();

		}

	}

	/**
	 * Called when has to update the components is some repositories.
	 *
	 * @param repositories of the components to update.
	 * @param pageOffset   the index of the current page.
	 */
	private void udateComponents(List<GitHubRepository> repositories, int pageOffset) {

		if (repositories.isEmpty()) {

			final var newOffset = pageOffset + 1;
			this.apiService.getVALAWAIRepositories(10, newOffset).onFailure().recoverWithItem(error -> {

				AddLog.fresh().withError(error)
						.withMessage(
								"Cannot update the components library, because can not get the GitHub repositories.")
						.store();
				return null;

			}).onItem().invoke(foundRepositories -> {

				if (foundRepositories == null || foundRepositories.isEmpty()) {
					// No more components to update
					AddLog.fresh().withInfo().withMessage("Updated the components library").store();
					this.configService
							.setProperty(MOVSettings.COMPONENTS_LIBRARY_LAST_UPDATE, String.valueOf(TimeManager.now()))
							.await().indefinitely();

				} else {

					this.udateInBackground(foundRepositories, newOffset);
				}

			}).await().indefinitely();

		} else {

			final var repository = repositories.remove(0);
			if (repository.name == null || repository.html_url == null
					|| !repository.name.matches("^[C|c][0|1|2]_.+$")) {

				this.udateInBackground(repositories, pageOffset);

			} else {

				final Uni<ComponentDefinitionEntity> find = ComponentDefinitionEntity
						.find("repository.html_url = ?1", repository.html_url).firstResult();
				find.onItem().ifNull().continueWith(() -> {

					final var entity = new ComponentDefinitionEntity();
					// no exception thrown because it matches ^[C|c][0|1|2]_.+$, see upper
					entity.type = ComponentType.valueOf(repository.name.substring(0, 2).toUpperCase());
					return entity;

				}).chain(entity -> {

					final var updated = TimeManager.toTime(repository.updated_at);
					if (entity.updatedAt <= updated) {

						entity.repository = repository;
						return this.rawService.getVALAWAIComponentReadmeContent(repository)
								.chain(readme -> this.rawService.getVALAWAIComponentAsyncapiContent(repository)
										.chain(asyncapi -> this.updateComponent(entity, readme, asyncapi)));

					} else {

						return Uni.createFrom().nullItem();

					}

				}).onFailure().recoverWithItem(error -> {

					AddLog.fresh().withError(error)
							.withMessage("Cannot update the component of {0}.", repository.html_url).store();
					return null;

				}).onItem().invoke(() -> this.udateInBackground(repositories, pageOffset)).await().indefinitely();
			}
		}
	}

	/**
	 * Update the entity that contains the component definition.
	 *
	 * @param readme   the content of the {@code README.md}.
	 * @param asyncapi the content of the {@code asyncapi.yaml}.
	 * @param entity   to update.
	 *
	 * @return the entity with the name and type.
	 */
	private Uni<ComponentDefinitionEntity> updateComponent(ComponentDefinitionEntity entity, String readme,
			String asyncapi) {

		try {

			final var component = ComponentBuilder.fromAsyncapi(asyncapi);
			this.updateNameIn(entity, readme, component);
			this.updateDescriptionIn(entity, readme, component);
			this.updateDocsLinkIn(entity, readme);
			this.updateVersionIn(entity, readme);
			this.updateAPIVersionIn(entity, readme, component);
			entity.channels = component.channels;

			entity.updatedAt = TimeManager.now();
			Uni<ComponentDefinitionEntity> update = null;
			if (entity.id == null) {

				update = entity.persist();

			} else {

				update = entity.update();
			}

			return update.onItem().invoke(() -> AddLog.fresh().withDebug()
					.withMessage("Update the component {0} {1}.", entity.type, entity.name).store());

		} catch (final Throwable cause) {

			return Uni.createFrom().failure(cause);
		}

	}

	/**
	 * Update the name of the component.
	 *
	 * @param readme    the content of the {@code README.md}.
	 * @param component defined by the {@code asyncapi.yaml}.
	 * @param entity    to update.
	 */
	public void updateNameIn(ComponentDefinitionEntity entity, String readme, Component component) {

		final var nameMatcher = Pattern
				.compile("[\\-|\\s|\\*|\\_]+name[\\*|\\_|\\s|\\:]+([^\\n]+)", Pattern.CASE_INSENSITIVE).matcher(readme);
		if (nameMatcher.find()) {

			entity.name = nameMatcher.group(1).trim();

		} else if (component != null && component.name != null) {

			entity.name = component.name;

		} else {

			entity.name = entity.repository.name.substring(3).replaceAll("\\W", " ");

		}

	}

	/**
	 * Update the description of the component.
	 *
	 * @param readme    the content of the {@code README.md}.
	 * @param component defined by the {@code asyncapi.yaml}.
	 * @param entity    to update.
	 */
	public void updateDescriptionIn(ComponentDefinitionEntity entity, String readme, Component component) {

		final var descriptionMatcher = Pattern.compile("^#[^\\n]+([^\\#]+)", Pattern.CASE_INSENSITIVE).matcher(readme);
		if (descriptionMatcher.find()) {

			entity.description = descriptionMatcher.group(1).replace('\n', ' ');

		} else if (component != null && component.description != null) {

			entity.description = component.description;

		} else {

			entity.description = entity.repository.description;
		}
		if (entity.description != null) {

			entity.description = entity.description.trim();
			if (entity.description.length() == 0) {
				entity.description = null;
			}
		}
	}

	/**
	 * Update the document link of the component.
	 *
	 * @param readme the content of the {@code README.md}.
	 * @param entity to update.
	 */
	public void updateDocsLinkIn(ComponentDefinitionEntity entity, String readme) {

		final var docsLinkPattern = Pattern.compile(
				"(https://valawai.github.io/docs/components/" + entity.toString() + "/[^\\/\\s\\]\\)]+)",
				Pattern.CASE_INSENSITIVE);
		final var docsLinkMatcher = docsLinkPattern.matcher(readme);
		if (docsLinkMatcher.find()) {

			entity.docsLink = docsLinkMatcher.group(1);

		} else {

			entity.docsLink = "https://valawai.github.io/docs/components/" + entity.type.name() + "/"
					+ entity.repository.name.substring(3);
		}
	}

	/**
	 * Update the version of the component.
	 *
	 * @param readme the content of the {@code README.md}.
	 * @param entity to update.
	 */
	public void updateVersionIn(ComponentDefinitionEntity entity, String readme) {

		final var versionPattern = Pattern.compile(
				"version[\\:|\\*|\\_|\\s|\\[]*(\\d+\\.\\d+\\.\\d+)\\s*(\\([^\\)]+\\))?", Pattern.CASE_INSENSITIVE);
		final var versionMatcher = versionPattern.matcher(readme);
		if (versionMatcher.find()) {

			if (entity.version == null) {

				entity.version = new VersionInfo();
			}

			entity.version.name = versionMatcher.group(1);
			entity.version.since = this.versionDateToInstant(versionMatcher.group(2));
		}
	}

	/**
	 * Obtain the time from a version date.
	 *
	 * @param versionDate to obtain the time.
	 *
	 * @return the time associated to the version date.
	 */
	private Long versionDateToInstant(String versionDate) {

		if (versionDate != null && versionDate.length() > 6) {

			try {

				final var dateString = versionDate.substring(1, versionDate.length() - 1).trim();
				final var formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy").withLocale(Locale.US);
				final var date = LocalDate.parse(dateString, formatter).atStartOfDay(ZoneId.of("UTC"));
				final var instant = date.toInstant();
				return TimeManager.toTime(instant);

			} catch (final Throwable error) {

				Log.debug("Cannot parse version date.", error);
			}

		}
		return null;

	}

	/**
	 * Update the API version of the component.
	 *
	 * @param readme    the content of the {@code README.md}.
	 * @param component defined by the {@code asyncapi.yaml}.
	 * @param entity    to update.
	 */
	public void updateAPIVersionIn(ComponentDefinitionEntity entity, String readme, Component component) {

		final var versionPattern = Pattern.compile("api[\\:|\\*|\\_|\\s|\\[]*(\\d+\\.\\d+\\.\\d+)\s*(\\([^\\)]+\\))?",
				Pattern.CASE_INSENSITIVE);
		final var versionMatcher = versionPattern.matcher(readme);
		if (versionMatcher.find()) {

			if (entity.apiVersion == null) {

				entity.apiVersion = new VersionInfo();
			}
			entity.apiVersion.name = versionMatcher.group(1);
			entity.apiVersion.since = this.versionDateToInstant(versionMatcher.group(2));

		} else {

			if (entity.apiVersion == null) {

				entity.apiVersion = new VersionInfo();
			}

			if (component != null) {

				entity.apiVersion.name = component.apiVersion;
				entity.apiVersion.since = TimeManager.toTime(entity.repository.updated_at);
			}
		}
	}

}
