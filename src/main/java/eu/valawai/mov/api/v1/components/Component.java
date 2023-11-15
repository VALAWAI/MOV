/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.events.ComponentType;
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
	 * The name of the component.
	 */
	@Schema(title = "The component name.")
	@Pattern(regexp = "c[0|1|2]_\\w+")
	public String name;

	/**
	 * The version of the components.
	 */
	@Schema(title = "The component version.")
	@NotEmpty
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The type of component.
	 */
	@Schema(title = "The component type.")
	@NotNull
	public ComponentType type;
}
