/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.components;

import java.io.Serializable;
import java.util.List;

import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.Component;
import eu.valawai.mov.api.v1.components.ComponentType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * The entity that contains the data of a component.
 *
 * @see Component
 *
 * @author VALAWAI
 */
@MongoEntity(collection = ComponentEntity.COLLECTION_NAME)
public class ComponentEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the collection with the components.
	 */
	public static final String COLLECTION_NAME = "components";

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the component.
	 */
	public String name;

	/**
	 * The description of the component.
	 */
	public String description;

	/**
	 * The version of the components.
	 */
	public String version;

	/**
	 * The version of the API.
	 */
	public String apiVersion;

	/**
	 * The type of component.
	 */
	public ComponentType type;

	/**
	 * The time when the component is registered.
	 */
	public long since;

	/**
	 * The time when the component is removed.
	 */
	public Long finishedTime;

	/**
	 * The channels defined on the component.
	 */
	public List<ChannelSchema> channels;

}
