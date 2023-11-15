/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * The api of a component to register.
 *
 * @see RegisterComponentPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "The API that the component implements")
public class ComponentApiInfo extends Payload {

	/**
	 * The version of the api.
	 */
	@Schema(title = "The API version.")
	@NotNull
	@Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
	public String version;

	/**
	 * The version of the api.
	 */
	@Schema(title = "The asyncapi specification.")
	@NotEmpty
	public String yaml;

}
