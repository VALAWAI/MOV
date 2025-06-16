/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import eu.valawai.mov.MOVSettings;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import eu.valawai.mov.persistence.live.logs.AddLog;
import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
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

			}).onItem().call(foundRepositories -> {

				if (foundRepositories == null || foundRepositories.isEmpty()) {
					// No more components to update
					AddLog.fresh().withInfo().withMessage("Updated the components library").store();
					this.configService
							.setProperty(MOVSettings.COMPONENTS_LIBRARY_LAST_UPDATE, String.valueOf(TimeManager.now()))
							.await().indefinitely();

				} else {

					this.udateInBackground(foundRepositories, newOffset);
				}
				return null;

			}).await().indefinitely();

		} else {

			final var repository = repositories.remove(0);
			if (repository.name == null || !repository.name.matches("^[C|c][0|1|2]_.+$")) {

				this.udateInBackground(repositories, pageOffset);

			} else {

				this.rawService.getVALAWAIComponentReadmeContent(repository).chain(
						readme -> this.rawService.getVALAWAIComponentAsyncapiContent(repository).chain(asyncapi -> {

							return this.searchByNameAndType(repository, readme, asyncapi)
									.chain(entity -> this.updateComponent(repository, readme, asyncapi, entity));

						})).onFailure().recoverWithItem(error -> {

							AddLog.fresh().withError(error)
									.withMessage("Cannot update the repository of {0}.", repository.name).store();
							return null;

						}).onItem().call(() -> {

							AddLog.fresh().withDebug().withMessage("Updated repository of {0}.", repository.name)
									.store();
							this.udateInBackground(repositories, pageOffset);
							return null;

						}).await().indefinitely();
			}
		}

	}

	/**
	 * Search for the {@link ComponentDefinitionEntity} by name and type, and then
	 * update it or create it.
	 *
	 * @param repository the repository of the component.
	 * @param readme     the content of the {@code README.md}.
	 * @param asyncapi   the content of the {@code asyncapi.yaml}.
	 *
	 * @return the entity with the name and type.
	 */
	private Uni<ComponentDefinitionEntity> searchByNameAndType(GitHubRepository repository, String readme,
			String asyncapi) {

		try {

			final var type = ComponentType.valueOf(repository.name.substring(0, 2).toUpperCase());

			final List<Bson> nameFilters = new ArrayList<>();
			nameFilters.add(Filters.regex("name", repository.name, "i"));
			var repoName = repository.name.substring(3);
			nameFilters.add(Filters.regex("name", repoName, "i"));
			repoName = repoName.replaceAll("\\W", " ").trim();
			nameFilters.add(Filters.regex("name", repoName, "i"));

			final var nameMatcher = Pattern.compile("^[\\- \\*]+name[\\* \\:]+(.+)$", Pattern.CASE_INSENSITIVE)
					.matcher(readme);
			final var name = nameMatcher.find() ? nameMatcher.group(1) : repoName;
			if (name != repoName) {

				nameFilters.add(Filters.regex("name", name, "i"));
			}

			final var query = Filters.and(Filters.eq("type", type), Filters.or(nameFilters));
			final var options = new FindOptions().sort(Sorts.ascending("id")).limit(1);
			final ReactiveMongoCollection<ComponentDefinitionEntity> collection = ComponentDefinitionEntity
					.mongoCollection();
			final Uni<ComponentDefinitionEntity> search = collection.find(query, options).collect().first();
			return search.map(component -> {

				if (component == null) {

					component = new ComponentDefinitionEntity();
					component.type = type;
				}
				component.name = name;
				return component;

			});

		} catch (final Throwable cause) {

			return Uni.createFrom().failure(cause);
		}

	}

	/**
	 * Update the entity that contains the component definition.
	 *
	 * @param repository the repository of the component.
	 * @param readme     the content of the {@code README.md}.
	 * @param asyncapi   the content of the {@code asyncapi.yaml}.
	 * @param entity     to update.
	 *
	 * @return the entity with the name and type.
	 */
	private Uni<ComponentDefinitionEntity> updateComponent(GitHubRepository repository, String readme, String asyncapi,
			ComponentDefinitionEntity entity) {

		try {

			final var docsLinkPattern = Pattern.compile(
					"(https://valawai.github.io/docs/components/" + entity.toString() + "/[^\\/\\s\\]\\)]+)",
					Pattern.CASE_INSENSITIVE);
			var docsLinkMatcher = docsLinkPattern.matcher(readme);
			if (docsLinkMatcher.find()) {

				entity.docsLink = docsLinkMatcher.group(1);

			} else {

				docsLinkMatcher = docsLinkPattern.matcher(asyncapi);
				if (docsLinkMatcher.find()) {

					entity.docsLink = docsLinkMatcher.group(1);

				}
			}

			entity.updatedAt = TimeManager.now();
			if (entity.id == null) {

				return entity.persist();

			} else {

				return entity.update();
			}

		} catch (final Throwable cause) {

			return Uni.createFrom().failure(cause);
		}

	}
}
