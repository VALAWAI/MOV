/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.components;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A page of the {@link ComponentDefinition} that satisfy a query.
 *
 * @see ComponentDefinition
 *
 * @author VALAWAI
 */
@Schema(description = "A page with some component definitions.")
public class ComponentDefinitionPage extends Model {

	/**
	 * The number of components that satisfy the query.
	 */
	@Schema(description = "The total number of components that satisfy the query.")
	public long total = 0;

	/**
	 * The offset of the first returned log.
	 */
	@Schema(description = "The index of the first returned log.")
	public int offset = 0;

	/**
	 * The components that match the query.
	 */
	@Schema(description = "The components that satisfy the query")
	public List<ComponentDefinition> components;

}
