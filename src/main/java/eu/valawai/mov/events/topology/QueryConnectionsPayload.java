/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.events.Payload;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * The information necessary to query for some connections.
 *
 * @author VALAWAI
 */
@JsonRootName("query_connections_payload")
public class QueryConnectionsPayload extends Payload {

	/**
	 * The identifier of the query.
	 */
	public String id;

	/**
	 * The name to match the source channel of the connections to return. If it is
	 * defined between / it is considered a PCRE regular expression.
	 */
	@JsonProperty("source_channel_name")
	public String sourceChannelName;

	/**
	 * The identifier to match the source component of the connections to return. If
	 * it is defined between / it is considered a PCRE regular expression.
	 */
	@JsonProperty("source_component_id")
	public String sourceComponentId;

	/**
	 * The name to match the target channel of the connections to return. If it is
	 * defined between / it is considered a PCRE regular expression.
	 */
	@JsonProperty("target_channel_name")
	public String targetChannelName;

	/**
	 * The identifier to match the target component of the connections to return. If
	 * it is defined between / it is considered a PCRE regular expression.
	 */
	@JsonProperty("target_component_id")
	public String targetComponentId;

	/**
	 * The order in witch the connections has to be returned. It is form by the
	 * field names, separated by a comma, and each of it with the - prefix for
	 * descending order or + for ascending.
	 */
	@Pattern(regexp = "(,?[+|-]?[createTimestamp|updateTimestamp|enabled|source.componentId|source.channelName|target.componentId|target.channelName])*")
	public String order = DEFAULT_ORDER;

	/**
	 * The default order.
	 */
	public static String DEFAULT_ORDER = "+createTimestamp";

	/**
	 * The index of the first connection to return.
	 */
	@Min(0)
	public int offset;

	/**
	 * The maximum number of connections to return.
	 */
	@Min(1)
	public int limit = DEFAULT_LIMIT;

	/**
	 * The default limit.
	 */
	public static int DEFAULT_LIMIT = 20;

}
