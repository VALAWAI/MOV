/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * The name of the component.
 *
 * @author VALAWAI
 */
@Schema(title = "A VALAWAI component.")
public class Component extends MinComponent {

	/**
	 * The version of the components.
	 */
	@Schema(title = "The component version.")
	@NotEmpty
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The version of the API.
	 */
	@Schema(title = "The component API version.")
	@NotEmpty
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String apiVersion;

	/**
	 * The epoch time in seconds when the component is registered.
	 */
	@Schema(title = "The time when the component is registered. The epoch time in seconds when the component is registered", readOnly = true)
	public long since;

	/**
	 * The channels defined on the component.
	 */
	@Schema(title = "The channel associated to the component.")
	public List<ChannelSchema> channels;

}
