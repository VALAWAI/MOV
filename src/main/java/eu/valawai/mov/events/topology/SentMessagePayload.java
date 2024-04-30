/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.v1.components.BasicPayloadFormat;
import eu.valawai.mov.api.v1.components.BasicPayloadSchema;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.api.v1.components.EnumPayloadSchema;
import eu.valawai.mov.api.v1.components.ObjectPayloadSchema;
import eu.valawai.mov.api.v1.components.PayloadSchema;
import eu.valawai.mov.events.Payload;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.quarkus.vertx.runtime.jackson.JsonObjectDeserializer;
import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotNull;

/**
 * The content of the message to notify a C2 component that a message is
 * interchanged between to non c2 components.
 *
 * @author VALAWAI
 */
public class SentMessagePayload extends Payload {

	/**
	 * The identifier of the topology connection that allows the message
	 * interchanging.
	 */
	@NotNull
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonProperty("connection_id")
	public ObjectId connectionId;

	/**
	 * The source component that has sent the message.
	 */
	public MinComponentPayload source;

	/**
	 * The target component that has received the message.
	 */
	public MinComponentPayload target;

	/**
	 * The content of the message.
	 */
	@JsonDeserialize(using = JsonObjectDeserializer.class)
	public JsonObject content;

	/**
	 * The epoch time, in seconds, when the message was sent.
	 */
	public long timestamp;

	/**
	 * Create the schema for a {@link SentMessagePayload} with specified content
	 * schema.
	 *
	 * @param content schema of the sent content.
	 *
	 * @return the schema for the sent.
	 */
	public static ObjectPayloadSchema createSentMessagePayloadSchemaFor(PayloadSchema content) {

		final ObjectPayloadSchema sentSchema = new ObjectPayloadSchema();
		final var strSchema = new BasicPayloadSchema();
		strSchema.format = BasicPayloadFormat.STRING;
		sentSchema.properties.put("connection_id", strSchema);

		final var address = new ObjectPayloadSchema();
		address.properties.put("id", strSchema);
		address.properties.put("name", strSchema);
		final var type = new EnumPayloadSchema();
		type.values.add(ComponentType.C0.name());
		type.values.add(ComponentType.C1.name());
		type.values.add(ComponentType.C2.name());
		address.properties.put("type", type);

		sentSchema.properties.put("source", address);
		sentSchema.properties.put("target", address);

		sentSchema.properties.put("content", content);
		final var timestamp = new BasicPayloadSchema();
		timestamp.format = BasicPayloadFormat.INTEGER;
		sentSchema.properties.put("timestamp", timestamp);

		return sentSchema;
	}
}
