/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
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
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "A schema that define the messages that a channel can receive or send.")
public class ChannelSchema extends Model {

	/**
	 * The identifier of the channel.
	 */
	@NotEmpty
	@Schema(title = "The identifier of the channel.")
	public String id;

	/**
	 * The description of the channel.
	 */
	@Schema(title = "The channel description.")
	public String description;

	/**
	 * The type of payload that the channel can receive.
	 */
	@Schema(title = "The content of the messages that the component can receive form this channel.")
	public PayloadSchema subscribe;

	/**
	 * The type of payload that the channel can send.
	 */
	@Schema(title = "The content of the messages that the component can send thought this channel.")
	public PayloadSchema publish;
}
