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
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * The description of a topology notification that has to be created in the
 * topology.
 *
 * @see ConnectionsPagePayload
 *
 * @author VALAWAI
 */
@JsonRootName("create_notification_payload")
public class CreateNotificationPayload extends Payload {

	/**
	 * The identifier of the topology connection where the notification is defined.
	 */
	@NotNull
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonProperty("connection_id")
	public ObjectId connectionId;

	/**
	 * The node that is the target of the notification.
	 */
	@NotNull
	public NodePayload target;

	/**
	 * This is {@code true} if the notification has to be enabled.
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

}
