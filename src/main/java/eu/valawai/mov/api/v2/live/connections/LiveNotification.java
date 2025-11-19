/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionNotification;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * A notification to do when a message pass though a connection.
 *
 * @see LiveConnection
 * @see TopologyConnectionNotification
 *
 * @author VALAWAI
 */
@Schema(description = "A notification to do when a message pass though a connection.")
public class LiveNotification extends Model {

	/**
	 * The end point of a component to notify.
	 */
	@Schema(description = "The end point of a component to notify.")
	@NotNull
	public LiveEndPoint node;

	/**
	 * This is true if the notification is enabled.
	 */
	@Schema(description = "This is true if the notification is enabled.")
	@NotNull
	public boolean enabled;

	/**
	 * The javaScript code that will be executed to convert the message that go from
	 * the source to the target to the message that the notification node can
	 * process.
	 */
	@Schema(description = "The javaScript code that will be executed to convert the message that go from the source to the target to the message that the notification node can process.")
	@Nullable
	public String convertCode;

}
