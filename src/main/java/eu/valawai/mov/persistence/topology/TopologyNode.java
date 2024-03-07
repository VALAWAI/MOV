/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;

/**
 * A node that form part of the topology.
 *
 * @see TopologyConnectionEntity#source
 * @see TopologyConnectionEntity#target
 *
 * @author VALAWAI
 */
public class TopologyNode extends Model {

	/**
	 * The identifier of the component that the topology connection starts or ends.
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId componentId;

	/**
	 * The name of the channel of the component that do the connection.
	 */
	public String channelName;

}
