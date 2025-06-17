/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import java.io.Serializable;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v2.design.components.VersionInfo;
import eu.valawai.mov.services.GitHubRepository;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * Define the information of a component that can be used to define a topology.
 *
 * @author VALAWAI
 */
@MongoEntity(collection = ComponentDefinitionEntity.COLLECTION_NAME)
public class ComponentDefinitionEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the collection with the component definitions.
	 */
	public static final String COLLECTION_NAME = "componentDefinitions";

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type of the component.
	 */
	public ComponentType type;

	/**
	 * The name of the component.
	 */
	public String name;

	/**
	 * The description of the element.
	 */
	public String description;

	/**
	 * The URL to the documentation of the component.
	 */
	public String docsLink;

	/**
	 * The repository where the component is defined.
	 */
	public GitHubRepository repository;

	/**
	 * The version of the component.
	 */
	public VersionInfo version;

	/**
	 * The API version of the component.
	 */
	public VersionInfo apiVersion;

	/**
	 * The channels that the component has.
	 */
	public List<ChannelSchema> channels;

	/**
	 * The epoch time, in seconds, when the definition is updated.
	 */
	@Schema(title = "The epoch time, in seconds, when the definition is updated.", example = "1640995200")
	public Long updatedAt;

}
