/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotNull;

/**
 * The content of the message to notify a C2 component that a message is
 * interchanged between to non c2 components.
 *
 * @author VALAWAI
 */
public class SentMessagePayload extends Payload {

	/**
	 * The identifier of the topology connection that allows the message
	 * interchanging.
	 */
	@NotNull
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonProperty("connection_id")
	public ObjectId connectionId;

	/**
	 * The source component that has sent the message.
	 */
	public MinComponentPayload source;

	/**
	 * The target component that has received the message.
	 */
	public MinComponentPayload target;

	/**
	 * The content of the message.
	 */
	public JsonObject content;

	/**
	 * The epoch time, in seconds, when the message was sent.
	 */
	public long timestamp;

}
