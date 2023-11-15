/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.valawai.mov.api.v1.help;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * Contains information about the web services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(title = "Information about the web service.")
public class Info extends Model {

	/**
	 * The name of the api.
	 */
	@Schema(title = "Name of the web services.", example = "eduteams-api")
	public String name;

	/**
	 * The version of the api.
	 */
	@Schema(title = "Version of the web services.", example = "1.0.0")
	public String version;

	/**
	 * The profile of the quarkus platform.
	 */
	@Schema(title = "Profile that start the quarkus", example = "production")
	public String profile;

}
