/*
  Copyright 2023 UDT-IA, IIIA-CSIC

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
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

/**
 * The information necessary to change a notification in a the topology
 * connection.
 *
 * @author VALAWAI
 */
@RegisterForReflection
@JsonRootName("change_notification_payload")
public class ChangeNotificationPayload extends Payload {

	/**
	 * The type of action to do on the notification defined in the topology
	 * connection.
	 */
	@NotNull
	public TopologyAction action;

	/**
	 * The identifier of the topology connection where is the notification to
	 * change.
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

}
