/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.events.Payload;
import jakarta.validation.constraints.Min;

/**
 * The connections that satisfy a query.
 *
 * @see QueryConnectionsPayload
 *
 * @author VALAWAI
 */
@JsonRootName("connections_page_payload")
public class ConnectionsPagePayload extends Payload {

	/**
	 * The identifier of the query that this is the answer.
	 */
	@JsonProperty("query_id")
	public String queryId;

	/**
	 * The number of connections that satisfy the query.
	 */
	@Min(0)
	public long total;

	/**
	 * The connections that satisfy the query.
	 */
	public List<ConnectionPayload> connections;

}
