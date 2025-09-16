/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.HashMap;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import jakarta.validation.constraints.NotEmpty;

/**
 * The definition of a channel that is defined on a {@link Component}.
 *
 * @see Component
 *
 * @author VALAWAI
 */
@Schema(description = "A schema that define the messages that a channel can receive or send.")
public class ChannelSchema extends Model {

	/**
	 * The identifier of the channel.
	 */
	@NotEmpty
	@Schema(description = "The name that identify the channel.")
	public String name;

	/**
	 * The description of the channel.
	 */
	@Schema(description = "The channel description.")
	public String description;

	/**
	 * The type of payload that the channel can receive.
	 */
	@Schema(description = "The content of the messages that the component can receive form this channel.")
	public PayloadSchema subscribe;

	/**
	 * The type of payload that the channel can send.
	 */
	@Schema(description = "The content of the messages that the component can send thought this channel.")
	public PayloadSchema publish;

	/**
	 * Check if this channel match another.
	 *
	 * @param other to check.
	 *
	 * @return {@code true} if this channels is equivalent to the other schema.
	 */
	public boolean match(ChannelSchema other) {

		if (other == null || this.name == null && other.name != null
				|| this.name != null && !this.name.equals(other.name)) {

			return false;

		} else if (this.subscribe != null) {

			return this.subscribe.match(other.subscribe, new HashMap<>());

		} else if (this.publish != null) {

			return this.publish.match(other.publish, new HashMap<>());

		} else {

			return false;
		}

	}

}
