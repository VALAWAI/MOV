/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the component that needs to be notified whenever a message passes
 * through a specific {@link TopologyConnection}.
 *
 * @see TopologyConnection#notifications
 *
 * @author VALAWAI
 */
@Schema(description = "Represents the component that needs to be notified whenever a message passes through a specific connection.")
public class TopologyConnectionNotification extends Model {

	/**
	 * The **recipient or delivery endpoint** for the notification, specifying the
	 * target's unique identifier (tag) and the specific channel through which it
	 * should be delivered.
	 */
	@Schema(description = "The recipient or delivery endpoint of the notification, including its tag and delivery channel.")
	@NotNull
	public TopologyConnectionEndpoint target;

	/**
	 * An optional code snippet or identifier used to transform the notification's
	 * content from its original format to the format expected by the target
	 * channel. Can be null or empty if no transformation is required.
	 */
	@Schema(description = "An optional code or identifier for transforming the notification's content to the target channel's format.")
	public String convertCode;
}
