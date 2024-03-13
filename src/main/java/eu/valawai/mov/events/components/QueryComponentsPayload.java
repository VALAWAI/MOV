/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import com.fasterxml.jackson.annotation.JsonRootName;

import eu.valawai.mov.events.Payload;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * The information necessary to query for some components.
 *
 * @author VALAWAI
 */
@JsonRootName("query_components_payload")
public class QueryComponentsPayload extends Payload {

	/**
	 * The identifier of the query.
	 */
	public String id;

	/**
	 * The pattern to match the name or description of the components to return. If
	 * it is defined between / it is considered a PCRE regular expression.
	 */
	public String pattern;

	/**
	 * The type to match the components to return. If it is defined between / it is
	 * considered a PCRE regular expression.
	 */
	public String type;

	/**
	 * The order in witch the components has to be returned. It is form by the field
	 * names, separated by a comma, and each of it with the - prefix for descending
	 * order or + for ascending.
	 */
	@Pattern(regexp = "(,?[+|-]?[type|name|description|since])*")
	public String order;

	/**
	 * The default order.
	 */
	public static String DEFAULT_ORDER = "+since";

	/**
	 * The index of the first component to return.
	 */
	@Min(0)
	public int offset;

	/**
	 * The maximum number of components to return.
	 */
	@Min(1)
	public int limit;

	/**
	 * The default limit.
	 */
	public static int DEFAULT_LIMIT = 20;

}
