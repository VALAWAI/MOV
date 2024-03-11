/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;

/**
 * The information necessary to unregister a component.
 *
 * @author VALAWAI
 */
@RegisterForReflection
@JsonRootName("unregister_component_payload")
@Schema(title = "The information necessary to unregister a component.")
public class UnregisterComponentPayload extends Payload {

	/**
	 * The identifier of the component to unregister.
	 */
	@NotNull
	@Schema(title = "The identifier of the component to unregister.")
	@NotNull
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonProperty("component_id")
	public ObjectId componentId;

}
