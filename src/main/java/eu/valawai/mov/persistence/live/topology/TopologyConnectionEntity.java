/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.io.Serializable;
import java.util.List;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * The entity that contains the information of a connection that define the
 * topology between the components.
 *
 * @author VALAWAI
 */
@MongoEntity(collection = TopologyConnectionEntity.COLLECTION_NAME)
public class TopologyConnectionEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the collection with the logs.
	 */
	public static final String COLLECTION_NAME = "topologyConnection";

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The epoch time, in seconds, when the connection has been created.
	 */
	public long createTimestamp;

	/**
	 * The epoch time, in seconds, when the connection has been updated.
	 */
	public long updateTimestamp;

	/**
	 * The epoch time, in seconds, when the connection has been removed. If it is
	 * {@code null} the connection is active.
	 */
	public Long deletedTimestamp;

	/**
	 * The node that is the source of the connection.
	 */
	public TopologyNode source;

	/**
	 * The node that is the target of the connection.
	 */
	public TopologyNode target;

	/**
	 * This is {@code true} if the connection is enabled.
	 */
	public boolean enabled;

	/**
	 * The list of C2 components that will be notified when a message is send
	 * between the source and the target.
	 */
	public List<TopologyNode> c2Subscriptions;
}
