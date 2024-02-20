/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The generic component to encode and decode any {@link Model} using jackson.
 *
 * @param <T> the type of model to encode/decode.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class JacksonCodec<T> implements Codec<T> {

	/**
	 * The component to encode/decode values into JSON using jackson.
	 */
	private final ObjectMapper objectMapper;

	/**
	 * The encoded for a raw JSON document.
	 */
	private final Codec<RawBsonDocument> rawCodec;

	/**
	 * The type of class to encode/decode.
	 */
	private final Class<T> type;

	/**
	 * Create the component to encode/decode form raw jackson JSON documents.
	 *
	 * @param objectMapper to use.
	 * @param rawCodec     the encoder/decoder of raw JSON documents.
	 * @param type         of model to encode.
	 */
	public JacksonCodec(ObjectMapper objectMapper, Codec<RawBsonDocument> rawCodec, Class<T> type) {

		this.objectMapper = objectMapper;
		this.rawCodec = rawCodec;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #type
	 */
	@Override
	public Class<T> getEncoderClass() {

		return this.type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		try {

			final RawBsonDocument document = this.rawCodec.decode(reader, decoderContext);
			String json = document.toJson();
			json = json.replaceAll("\\{\\s*\"\\$oid\"\\s*\\:\\s*\"([0-9a-fA-F]+)\"\\s*\\}", "\"$1\"");
			json = json.replace("\"_id\"", "\"id\"");
			final var model = this.objectMapper.readValue(json, this.type);
			return model;

		} catch (final IOException e) {

			throw new UncheckedIOException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {

		try {

			final String json = this.objectMapper.writeValueAsString(value);
			this.rawCodec.encode(writer, RawBsonDocument.parse(json), encoderContext);

		} catch (final IOException e) {

			throw new UncheckedIOException(e);
		}

	}
}