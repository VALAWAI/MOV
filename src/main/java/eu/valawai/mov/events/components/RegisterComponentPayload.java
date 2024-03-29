/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.Payload;
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
public class RegisterComponentPayload extends Payload {

	/**
	 * The type of the component to register.
	 */
	@NotNull
	public ComponentType type;

	/**
	 * The name of the component to register.
	 */
	@Pattern(regexp = "c[0|1|2]_\\w+")
	@NotNull
	public String name;

	/**
	 * The version of the component.
	 */
	@NotNull
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The asyncapi specification in yaml.
	 */
	@NotEmpty
	@JsonProperty("asyncapi_yaml")
	public String asyncapiYaml;

}
