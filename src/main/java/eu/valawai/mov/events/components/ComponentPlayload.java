/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;

/**
 * A payload associated to a component
 *
 * @author VALAWAI
 */
public class ComponentPlayload extends Payload {

	/**
	 * The identifier of the component associated to the payload.
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId componentId;

}
