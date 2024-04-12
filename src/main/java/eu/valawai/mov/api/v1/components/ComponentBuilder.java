/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.quarkus.logging.Log;

/**
 * The utility class to create a {@link Component}.
 *
 * @see Component
 *
 * @author VALAWAI
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

				for (final var channelId : channels.keySet()) {

					@SuppressWarnings("unchecked")
					final var channelDef = (Map<String, Object>) channels.get(channelId);
					final var channel = new ChannelSchema();
					component.channels.add(channel);
					channel.name = channelId;
					channel.description = stringProperty("description", channelDef);
					final var subscribe = section("subscribe", channelDef);
					var message = section("message", subscribe);
					channel.subscribe = messagePayload(message, asyncapi);
					final var publish = section("publish", channelDef);
					message = section("message", publish);
					channel.publish = messagePayload(message, asyncapi);
				}

				return component;
			}

		} catch (final Throwable error) {

			Log.debugv(error, "Cannot build a component with {0}", api);
		}
		return null;

	}

	/**
	 * Obtain the payload of a message schema.
	 *
	 * @param message  to get the payload.
	 * @param document where are the references.
	 *
	 * @return the payload associated to the message.
	 */
	private static PayloadSchema messagePayload(Map<String, Object> message, Map<String, Object> document) {

		var msg = referenceSection(message, document);
		if (msg == null) {

			msg = message;
		}
		final var payload = section("payload", msg);
		return payload(payload, document, new ArrayList<>());
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
	 * Obtain the payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param objects  the objects schemas that has already defined.
	 *
	 * @return the payload schema or {@code null} if cannot obtain it.
	 */
	private static PayloadSchema payload(Map<String, Object> section, Map<String, Object> document,
			List<ObjectPayloadSchema> objects) {

		if (section == null) {

			return null;

		} else {

			final var referenceSection = referenceSection(section, document);
			if (referenceSection != null) {

				return payload(referenceSection, document, objects);

			} else if (section.containsKey("const")) {

				return constantPayload(section, document);

			} else if (section.containsKey("enum")) {

				return enumPayload(section, document);

			} else if (section.containsKey("type")) {

				final var type = stringProperty("type", section);
				return switch (type) {
				case "integer" -> BasicPayloadSchema.with(BasicPayloadFormat.INTEGER);
				case "number" -> BasicPayloadSchema.with(BasicPayloadFormat.NUMBER);
				case "string" -> BasicPayloadSchema.with(BasicPayloadFormat.STRING);
				case "boolean" -> BasicPayloadSchema.with(BasicPayloadFormat.BOOLEAN);
				case "object" -> objectPayload(section, document, objects);
				default -> diversePayload(section, document, objects);
				};

			} else {

				return diversePayload(section, document, objects);
			}
		}
	}

	/**
	 * Obtain the enumeration payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 *
	 * @return the enumeration payload schema or {@code null} if cannot obtain it.
	 */
	private static EnumPayloadSchema enumPayload(Map<String, Object> section, Map<String, Object> document) {

		final var values = section.get("enum");
		final var payload = new EnumPayloadSchema();
		for (final var value : (Collection<?>) values) {

			final var element = String.valueOf(value);
			payload.values.add(element);

		}
		return payload;

	}

	/**
	 * Obtain the constant payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 *
	 * @return the constant payload schema or {@code null} if cannot obtain it.
	 */
	private static ConstantPayloadSchema constantPayload(Map<String, Object> section, Map<String, Object> document) {

		final var payload = new ConstantPayloadSchema();
		payload.value = (String) section.get("const");
		return payload;

	}

	/**
	 * Obtain the diverse payload schema defined.
	 *
	 * @param section  with the diverse references are defined.
	 * @param document where are the references.
	 * @param objects  the objects schemas that has already defined.
	 *
	 * @return the diverse payload schema or {@code null} if cannot obtain it.
	 */
	private static PayloadSchema diversePayload(Map<String, Object> section, Map<String, Object> document,
			List<ObjectPayloadSchema> objects) {

		DiversePayloadSchema schema = null;
		Object values = null;
		if ("array".equals(section.get("type"))) {

			values = section.get("items");
			schema = new ArrayPayloadSchema();

		} else if (section.containsKey("oneOf")) {

			values = section.get("oneOf");
			schema = new OneOfPayloadSchema();

		} else if (section.containsKey("anyOf")) {

			values = section.get("anyOf");
			schema = new AnyOfPayloadSchema();

		} else if (section.containsKey("allOf")) {

			values = section.get("allOf");
			schema = new AllOfPayloadSchema();

		} else {
			// it may be an object
			return objectPayload(section, document, objects);
		}

		if (values instanceof final List references) {

			final var iter = references.iterator();
			schema.items = new ArrayList<>();
			while (iter.hasNext()) {

				final var ref = iter.next();
				if (ref instanceof Map) {

					@SuppressWarnings("unchecked")
					final var item = payload((Map<String, Object>) ref, document, objects);
					if (item != null) {

						schema.items.add(item);
					}
				}
			}

		} else if (values instanceof final Map ref) {

			@SuppressWarnings("unchecked")
			final var item = payload(ref, document, objects);
			if (item != null) {

				schema.items = new ArrayList<>();
				schema.items.add(item);
			}

		} // else unexpected references definition

		if (schema != null && !(schema instanceof ArrayPayloadSchema)) {

			if (schema.items.size() == 1) {

				return schema.items.get(0);
			}
		}

		return schema;
	}

	/**
	 * Obtain the object payload schema defined.
	 *
	 * @param section  with the payload definition.
	 * @param document where are the references.
	 * @param objects  the objects schemas that has already defined.
	 *
	 * @return the object payload schema or {@code null} if cannot obtain it.
	 */
	private static PayloadSchema objectPayload(Map<String, Object> section, Map<String, Object> document,
			List<ObjectPayloadSchema> objects) {

		final var properties = section("properties", section);
		final var propertNames = properties.keySet();
		final var max = objects.size();
		for (var i = 0; i < max; i++) {

			final var object = objects.get(i);
			if (object.properties.keySet().equals(propertNames)) {

				object.id = i;
				final var ref = new ReferencePayloadSchema();
				ref.value = i;
				return ref;
			}
		}

		final var objPayload = new ObjectPayloadSchema();
		for (final var name : propertNames) {

			objPayload.properties.put(name, null);
		}
		objects.add(objPayload);

		for (final var name : propertNames) {

			final var payloadSection = section(name, properties);
			final var payload = payload(payloadSection, document, objects);
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
