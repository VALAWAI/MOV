/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.design.topology;

import java.io.Serializable;
import java.util.List;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * Represents a stored definition of a topology graph, including its metadata,
 * structural description, and the collection of nodes that compose it. This
 * entity is designed for persistence in a MongoDB database.
 *
 * @author VALAWAI
 */
@MongoEntity(collection = TopologyGraphEntity.COLLECTION_NAME)
public class TopologyGraphEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the MongoDB collection where TopologyGraphEntity instances are
	 * stored.
	 */
	public static final String COLLECTION_NAME = "topologyGraphs";

	/**
	 * Serialization identifier to ensure class compatibility during
	 * deserialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The human-readable name of this topology graph. This name should ideally be
	 * unique and descriptive, aiding in identification and management.
	 */
	public String name;

	/**
	 * An optional detailed description of the topology's purpose, design, or any
	 * relevant notes. This can be null or an empty string if no description is
	 * provided.
	 */
	public String description;

	/**
	 * The timestamp (epoch time in seconds) indicating the last time this topology
	 * graph was updated. This is automatically managed upon persistence.
	 */
	public long updatedAt;

	/**
	 * A list of {@link TopologyGraphNode} objects that constitute the components
	 * and their interconnections within this topology graph. This defines the
	 * structure of the entire graph.
	 */
	public List<TopologyGraphNode> nodes;

}
