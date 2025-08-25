/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.connections;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * A set of {@link LiveConnection}s that satisfy a query.
 *
 * @author VALAWAI
 */
@Schema(title = "A set of live connections that satisfy a query.")
public class LiveConnectionPage extends Model {

	/**
	 * The connections taht satisfy the query.
	 */
	@Schema(title = "The connections that satisfy the query.")
	public List<LiveConnection> connections;

	/**
	 * The number of connections that satisfy the query.
	 */
	@Schema(title = "The number of connections that satisfy the query.")
	public int total;
}
