/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The information necessary to register a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@RegisterForReflection
@JsonRootName("register_component_payload")
@Schema(title = "The information necessary to register a component.")
public class RegisterComponentPayload extends Payload {

	/**
	 * The type of the component to register.
	 */
	@NotNull
	@Schema(title = "The type of the component to register.")
	public ComponentType type;

	/**
	 * The name of the component to register.
	 */
	@NotNull
	@Pattern(regexp = "c[0|1|2]_\\w+")
	@Schema(title = "The component name.")
	public String name;

	/**
	 * The version of the component.
	 */
	@NotNull
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	@Schema(title = "The component version.")
	public String version;

	/**
	 * The API of the component.
	 */
	@NotNull
	@Schema(title = "The component API.")
	public ComponentApiInfo api;

}
