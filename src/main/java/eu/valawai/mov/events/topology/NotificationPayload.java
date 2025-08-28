/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.events.Payload;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * The description of a notification defined in a topology connection.
 *
 * @see ConnectionPayload#notifications
 *
 * @author VALAWAI
 */
@JsonRootName("notification_payload")
public class NotificationPayload extends Payload {

	/**
	 * The node that is the target of the notification.
	 */
	@NotNull
	public NodePayload target;

	/**
	 * This is {@code true} if the notification is enabled.
	 */
	@NotNull
	public boolean enabled;

	/**
	 * The javaScript code that will be executed to convert the message from the
	 * source to the message that the target of the notification can handle.
	 */
	@Nullable
	@JsonProperty("converter_js_code")
	public String converterJSCode;

}
