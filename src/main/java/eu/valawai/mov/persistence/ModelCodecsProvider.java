/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.valawai.mov.persistence;

import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.valawai.mov.api.v1.components.PayloadSchema;

/**
 * The provider for some models that can not be generated automatically.
 *
 * @see PayloadSchema
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ModelCodecsProvider implements CodecProvider {

	/**
	 * The component to encode/decode values into JSON using jackson.
	 */
	private final ObjectMapper objectMapper;

	/**
	 * Create the provider for the model codecs.
	 *
	 * @param bsonObjectMapper jackson service.
	 */
	public ModelCodecsProvider(final ObjectMapper bsonObjectMapper) {

		this.objectMapper = bsonObjectMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {

		if (PayloadSchema.class == clazz) {

			final var rawCodec = registry.get(RawBsonDocument.class);
			return new JacksonCodec<T>(this.objectMapper, rawCodec, clazz);

		} else {

			return null;
		}
	}

}
