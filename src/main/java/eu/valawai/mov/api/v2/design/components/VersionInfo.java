/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;
import jakarta.validation.constraints.NotEmpty;

/**
 * Contain the information of a version.
 *
 * @see ComponentDefinition
 *
 * @author VALAWAI
 */
@Schema(description = "The information of a version.")
public class VersionInfo extends Model {

	/**
	 * the name of the version.
	 */
	@Schema(title = "The name of the component.", examples = "1.0.0")
	@NotEmpty
	public String name;

	/**
	 * The epoch time, in seconds, when the version is set.
	 */
	@Schema(title = "The epoch time, in seconds, when the version is set.", examples = "1640995200")
	public Long since;

}
