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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The information of a component to register.
 *
 * @author VALAWAI
 */
@Schema(title = "The information of a component to register.")
public class ComponentToRegister extends Model {

	/**
	 * The type of the component to register.
	 */
	@Schema(title = "The type of the component to register.")
	@NotNull
	public ComponentType type;

	/**
	 * The name of the component to register.
	 */
	@Schema(title = "The component name.")
	@Pattern(regexp = "c[0|1|2]_\\w+")
	@NotNull
	public String name;

	/**
	 * The version of the component.
	 */
	@Schema(title = "The component version.")
	@NotNull
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The asyncapi specification in yaml.
	 */
	@Schema(title = "The asyncapi specification in yaml.")
	@NotEmpty
	public String asyncapiYaml;

}
