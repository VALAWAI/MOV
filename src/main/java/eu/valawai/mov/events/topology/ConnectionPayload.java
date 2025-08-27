/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The description of a topology connection that is in VALAWAI.
 *
 * @see ConnectionsPagePayload
 *
 * @author VALAWAI
 */
@JsonRootName("connection_payload")
public class ConnectionPayload extends Payload {

	/**
	 * The identifier of the topology connection.
	 */
	@BsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@Pattern(regexp = "[0-9a-fA-F]{24}")
	public ObjectId id;

	/**
	 * The epoch time, in seconds, when the connection has been created.
	 */
	@JsonProperty("create_timestamp")
	public long createTimestamp;

	/**
	 * The epoch time, in seconds, when the connection has been updated.
	 */
	@JsonProperty("update_timestamp")
	public long updateTimestamp;

	/**
	 * The node that is the source of the connection.
	 */
	@NotNull
	public NodePayload source;

	/**
	 * The node that is the target of the connection.
	 */
	@NotNull
	public NodePayload target;

	/**
	 * This is {@code true} if the connection is enabled.
	 */
	@NotNull
	public boolean enabled;

	/**
	 * The javaScript code that will be executed to convert the message from the
	 * source to the message that the target can handle.
	 */
	@Nullable
	@JsonProperty("converter_js_code")
	public String converterJSCode;

	/**
	 * The notifications of the connection.
	 */
	@Nullable
	public List<NotificationPayload> notifications;

}
