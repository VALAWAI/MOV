/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/
package eu.valawai.mov.api.v1.help;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * Contains information about the web services.
 *
 * @author VALAWAI
 */
@Schema(description = "Information about the web service.")
public class Info extends Model {

	/**
	 * The version of the api.
	 */
	@Schema(description = "Version of the web services.", example = "1.0.0")
	public String version;

	/**
	 * The profile of the quarkus platform.
	 */
	@Schema(description = "Profile that start the quarkus", example = "production")
	public String profile;

}
