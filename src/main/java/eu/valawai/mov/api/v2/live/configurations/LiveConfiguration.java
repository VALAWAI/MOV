/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.configurations;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.api.Model;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdDeserializer;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import jakarta.annotation.Nullable;

/**
 * The configuration of the MOV instance.
 *
 * @author VALAWAI
 */
@Schema(description = "The configuration of the MOV instance.")
public class LiveConfiguration extends Model {

	/**
	 * The identifier of the designed topology to follow in live.
	 */
	@Schema(description = "The identifier of the designed topology to follow in live.", readOnly = true, examples = "688cca9c7079a2f5e0f45ee1", implementation = String.class)
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	@Nullable
	public ObjectId topologyId;

	/**
	 * The behaviour to follow when a component is registered in the MOV.
	 */
	@Schema(description = "The behaviour to follow when a component is registered in the MOV.", readOnly = true, examples = "DO_NOTHING")
	@Nullable
	public TopologyBehavior registerComponent;

	/**
	 * The behaviour to follow when a component is registered in the MOV.
	 */
	@Schema(description = "The behaviour to follow when a connection is created in the MOV.", readOnly = true, examples = "DO_NOTHING")
	@Nullable
	public TopologyBehavior createConnection;

}
