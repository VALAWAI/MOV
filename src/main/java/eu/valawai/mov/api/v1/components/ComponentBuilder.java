/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.quarkus.logging.Log;

/**
 * The utility class to create a {@link Component}.
 *
 * @see Component
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface ComponentBuilder {

	/**
	 * Create the component form the AsyncApi description.
	 *
	 * @param api to create the component.
	 *
	 * @return the component that matches the AsyncApi or {@code null} if cannot
	 *         obtain it.
	 */
	public static Component fromAsyncapi(String api) {

		try {

			final Yaml yaml = new Yaml();
			final Map<String, Object> asyncapi = yaml.load(api);
			final var channels = section("channels", asyncapi);
			if (channels != null) {

				final var info = section("info", asyncapi);
				final var component = new Component();
				component.name = stringProperty("title", info);
				component.description = stringProperty("description", info);
				component.version = stringProperty("version", info);
				component.apiVersion = component.version;
				component.channels = new ArrayList<>();

				final Map<String, PayloadSchema> schemas = new HashMap<>();
				for (final var channelId : channels.keySet()) {

					@SuppressWarnings("unchecked")
					final var channelDef = (Map<String, Object>) channels.get(channelId);
					final var channel = new ChannelSchema();
					component.channels.add(channel);
					channel.id = channelId;
					channel.description = stringProperty("description", channelDef);
					final var subscribe = section("subscribe", channelDef);
					var message = section("message", subscribe);
					channel.subscribe = messagePayload(message, asyncapi, schemas);
					final var publish = section("publish", channelDef);
					message = section("message", publish);
					channel.publish = messagePayload(message, asyncapi, schemas);
				}

				return component;
			}

		} catch (final Throwable error) {

			Log.debugv(error, "Cannot build a component form {0}", api);
		}
		return null;

	}

	/**
	 * Obtain the payload of a message schema.
	 *
	 * @param message  to get the payload.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the payload associated to the message.
	 */
	private static PayloadSchema messagePayload(Map<String, Object> message, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		var msg = referenceSection(message, document);
		if (msg == null) {

			msg = message;
		}
		final var payload = section("payload", msg);
		return payload(payload, document, schemas);
	}

	/**
	 * Obtain the reference defined in a section.
	 *
	 * @param section  to obtain the reference.
	 * @param document where are the references.
	 *
	 * @return the reference section or {@code null} if not refers to anything.
	 */
	private static Map<String, Object> referenceSection(Map<String, Object> section, Map<String, Object> document) {

		Map<String, Object> subSection = null;
		if (section != null) {

			final var value = section.get("$ref");
			if (value instanceof final String ref) {

				subSection = document;
				final var path = ref.substring(2).split("\\/");
				for (final var element : path) {

					subSection = section(element, subSection);
				}
			}
		}
		return subSection;
	}

	/**
	 * Obtain the reference defined in a section.
	 *
	 * @param section  to obtain the reference.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the reference section or {@code null} if not refers to anything.
	 */
	private static PayloadSchema referencePayload(Map<String, Object> section, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		PayloadSchema payload = null;
		final var value = section.get("$ref");
		if (value instanceof final String ref) {

			final var defined = schemas.get(ref);
			if (defined instanceof PayloadSchema) {

				payload = defined;

			} else {

				final var referenceSection = referenceSection(section, document);
				payload = payload(referenceSection, document, schemas);
				schemas.put(ref, payload);
			}
		}
		return payload;
	}

	/**
	 * Obtain the payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the payload schema or {@code null} if cannot obtain it.
	 */
	private static PayloadSchema payload(Map<String, Object> section, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		if (section == null) {

			return null;

		} else {

			final var ref = referencePayload(section, document, schemas);
			if (ref != null) {

				return ref;

			} else if (section.containsKey("enum")) {

				return enumPayload(section, document, schemas);

			} else {

				final var type = stringProperty("type", section);
				return switch (type) {
				case "integer" -> BasicPayloadSchema.with(BasicPayloadFormat.INTEGER);
				case "number" -> BasicPayloadSchema.with(BasicPayloadFormat.NUMBER);
				case "string" -> BasicPayloadSchema.with(BasicPayloadFormat.STRING);
				case "boolean" -> BasicPayloadSchema.with(BasicPayloadFormat.BOOLEAN);
				case "array" -> arrayPayload(section, document, schemas);
				default -> objectPayload(section, document, schemas);
				};
			}
		}
	}

	/**
	 * Obtain the enumeration payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the enumeration payload schema or {@code null} if cannot obtain it.
	 */
	private static EnumPayloadSchema enumPayload(Map<String, Object> section, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		final var values = section.get("enum");
		final var payload = new EnumPayloadSchema();
		for (final var value : (Collection<?>) values) {

			final var element = String.valueOf(value);
			payload.values.add(element);

		}
		return payload;

	}

	/**
	 * Obtain the array payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the array payload schema or {@code null} if cannot obtain it.
	 */
	private static ArrayPayloadSchema arrayPayload(Map<String, Object> section, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		final var itemsSection = section("items", section);
		final var items = payload(itemsSection, document, schemas);
		final var array = new ArrayPayloadSchema();
		array.items = items;
		return array;
	}

	/**
	 * Obtain the object payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param schemas  the schemas that has already defined.
	 *
	 * @return the object payload schema or {@code null} if cannot obtain it.
	 */
	private static ObjectPayloadSchema objectPayload(Map<String, Object> section, Map<String, Object> document,
			Map<String, PayloadSchema> schemas) {

		final var properties = section("properties", section);
		final var objPayload = new ObjectPayloadSchema();
		for (final var name : properties.keySet()) {

			final var payloadSection = section(name, properties);
			final var payload = payload(payloadSection, document, schemas);
			objPayload.properties.put(name, payload);
		}

		return objPayload;
	}

	/**
	 * Get a string property form a section.
	 *
	 * @param name    of the property.
	 * @param section to get the property.
	 *
	 * @return the string value of the property or {@code null} if not defined.
	 */
	private static String stringProperty(String name, Map<String, Object> section) {

		if (section != null) {

			final var value = section.get(name);
			if (value instanceof final String property) {

				return property;
			}
		}
		return null;
	}

	/**
	 * Get a section of the AsyncApi.
	 *
	 * @param doc document to get the section.
	 *
	 * @return the section of the document or {@code null} if it is not defined.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> section(String name, Map<String, Object> doc) {

		if (doc != null) {

			final var section = doc.get(name);
			if (section instanceof Map) {

				return (Map<String, Object>) section;
			}

		}
		return null;
	}

}
