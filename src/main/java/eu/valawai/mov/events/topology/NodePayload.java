/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.validation.constraints.NotNull;

/**
 * A node that form part of the topology.
 *
 * @see ConnectionPayload#source
 * @see ConnectionPayload#target
 *
 * @author VALAWAI
 */
@JsonRootName("node_payload")
public class NodePayload extends Payload {

	/**
	 * The identifier of the component that the topology connection starts or ends.
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonProperty("component_id")
	@NotNull
	public ObjectId componentId;

	/**
	 * The name of the channel of the component that do the connection.
	 */
	@JsonProperty("channel_name")
	@NotNull
	public String channelName;

}
