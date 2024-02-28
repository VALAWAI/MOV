/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.api.v1.components.ComponentType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The information necessary to register a component.
 *
 * @author VALAWAI
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
	@Schema(title = "The component name.")
	@NotNull
	public String name;

	/**
	 * The version of the component.
	 */
	@NotNull
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	@Schema(title = "The component version.")
	public String version;

	/**
	 * The asyncapi specification in yaml.
	 */
	@Schema(title = "The asyncapi specification in yaml.")
	@NotEmpty
	public String asyncapiYaml;

}
