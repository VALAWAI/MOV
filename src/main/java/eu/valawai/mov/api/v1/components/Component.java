/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * The name of the component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "A VALAWAI component.")
public class Component extends Model {

	/**
	 * The identifier of the component.
	 */
	@Schema(title = "The component identifier.")
	public String id;

	/**
	 * The name of the component.
	 */
	@Schema(title = "The component name.")
	@NotEmpty
	public String name;

	/**
	 * The description of the component.
	 */
	@Schema(title = "The component description.")
	public String description;

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
	 * The type of component.
	 */
	@Schema(title = "The component type.")
	@NotNull
	public ComponentType type;

	/**
	 * The time when the component is registered.
	 */
	@Schema(title = "The time when the component is registered.")
	public long since;

	/**
	 * The channels defined on the component.
	 */
	@Schema(title = "The channel associated to the component.")
	public List<ChannelSchema> channels;

}
