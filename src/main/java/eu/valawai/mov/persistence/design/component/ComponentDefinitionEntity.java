/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.component;

import java.io.Serializable;
import java.util.List;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v2.design.components.VersionInfo;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import eu.valawai.mov.services.GitHubRepository;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * Defines the blueprint or specification for a reusable component that can be
 * instantiated and used within a {@link TopologyGraphEntity}. This entity
 * encapsulates all necessary information about a component, including its type,
 * naming, documentation, versioning, and channel schemas for communication. It
 * serves as a master record for available components in the system. *
 *
 * @author VALAWAI
 *
 * @see TopologyGraphEntity
 * @see ComponentType
 * @see GitHubRepository
 * @see VersionInfo
 * @see ChannelSchema
 */
@MongoEntity(collection = ComponentDefinitionEntity.COLLECTION_NAME)
public class ComponentDefinitionEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the MongoDB collection where component definition entities are
	 * stored. This constant ensures consistency across the application when
	 * referencing the collection.
	 */
	public static final String COLLECTION_NAME = "componentDefinitions";

	/**
	 * Serialization identifier. This field is used by the Java serialization
	 * mechanism to ensure that a serialized class can be deserialized by the same
	 * version of the class.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The categorical type of this component (e.g., 'Source', 'Processor', 'Sink').
	 * This helps in classifying and filtering components.
	 */
	public ComponentType type;

	/**
	 * The unique and human-readable name of this component definition. This name
	 * should ideally be unique across all component definitions.
	 */
	public String name;

	/**
	 * An optional, detailed description providing more context about the
	 * component's functionality, purpose, or use cases.
	 */
	public String description;

	/**
	 * A URL linking to external documentation or specifications for this component.
	 * This provides easy access to more in-depth information.
	 */
	public String docsLink;

	/**
	 * Information about the GitHub repository where the source code or definition
	 * of this component is maintained.
	 */
	public GitHubRepository repository;

	/**
	 * The specific version of this component definition. This tracks changes to the
	 * component's internal logic or features.
	 */
	public VersionInfo version;

	/**
	 * The version of the component's API (Application Programming Interface). This
	 * tracks compatibility changes in how the component interacts with others.
	 */
	public VersionInfo apiVersion;

	/**
	 * A list of {@link ChannelSchema} objects defining the input and/or output
	 * channels that this component possesses. Each schema specifies the channel's
	 * name, type, and expected data format.
	 */
	public List<ChannelSchema> channels;

	/**
	 * The timestamp, represented as epoch seconds, indicating the last time this
	 * component definition was updated or modified. This helps in tracking the
	 * freshness of component information.
	 */
	public long updatedAt;

}
