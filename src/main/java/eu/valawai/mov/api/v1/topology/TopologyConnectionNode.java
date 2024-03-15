/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.topology;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.api.v1.components.ChannelSchema;
import eu.valawai.mov.api.v1.components.MinComponent;
import jakarta.validation.constraints.NotNull;

/**
 * A node that define the source or target of a {@link TopologyConnection}.
 *
 * @see TopologyConnection#source
 * @see TopologyConnection#target
 *
 * @author VALAWAI
 */
@Schema(title = "The information of the node defined in a topology connection.")
public class TopologyConnectionNode extends Model {

	/**
	 * The component that is the node of the connection.
	 */
	@Schema(title = "The component that is the source/target of the connection.")
	@NotNull
	public MinComponent component;

	/**
	 * The channel thought the events are sent/received in the connection.
	 */
	@Schema(title = "The channel thought the events are sent/received in the connection.")
	@NotNull
	public ChannelSchema channel;

}
