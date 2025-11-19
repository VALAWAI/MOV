/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.api.v1.components.MinComponent;
import jakarta.validation.constraints.NotNull;

/**
 * An endpoint that a connection or a notification is connected to.
 *
 * @author VALAWAI
 */
@Schema(description = "An endpoint that a connection or a notification is connected to.")
public class LiveEndPoint extends Model {

	/**
	 * The component that the endpoint is connected to.
	 */
	@Schema(description = "The component that the end point is connected to.")
	@NotNull
	public MinComponent component;

	/**
	 * The name channel associated to the end point.
	 */
	@Schema(description = "The name of the channel associated to the end point.")
	@NotNull
	public String channelName;

}
