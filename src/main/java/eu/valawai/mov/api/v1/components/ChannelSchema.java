/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

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
}
