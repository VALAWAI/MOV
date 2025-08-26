/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.events.Payload;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * The description of a topology connection that is in VALAWAI.
 *
 * @see ConnectionsPagePayload
 *
 * @author VALAWAI
 */
@JsonRootName("create_connection_payload")
public class CreateConnectionPayload extends Payload {

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
	 * This is {@code true} if the connection has to be enabled.
	 */
	@NotNull
	public boolean enabled;

	/**
	 * The javaScript code that will be executed to convert the message from the
	 * source to the message that the target can handle.
	 */
	@Nullable
	public String target_message_converter_js_code;

}
