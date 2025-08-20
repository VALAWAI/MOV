/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.topology;

import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.api.Model;
import eu.valawai.mov.api.v1.components.ComponentType;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;

/**
 * A node that form part of the topology.
 *
 * @see TopologyConnectionEntity#source
 * @see TopologyConnectionEntity#target
 *
 * @author VALAWAI
 */
public class TopologyNode extends Model {

	/**
	 * The identifier of the component that the topology connection starts or ends.
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId componentId;

	/**
	 * The name of the channel of the component that do the connection.
	 */
	public String channelName;

	/**
	 * The regular expression to find a type in the channel name.
	 */
	static final Pattern COMPONENT_TYPE_IN_CHANNEL_PATTERN = Pattern.compile(".+\\/(c[012])\\/.*",
			Pattern.CASE_INSENSITIVE);
	/**
	 * The regular expression to find a type in the channel name.
	 */
	static final Pattern COMPONENT_NAME_IN_CHANNEL_PATTERN = Pattern.compile(".+\\/(c[012]\\/[^\\/]+).*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Infer the component type from the channel name.
	 *
	 * @return the component type.
	 */
	public ComponentType inferComponentType() {

		try {

			final var matcher = COMPONENT_TYPE_IN_CHANNEL_PATTERN.matcher(this.channelName);
			if (matcher.find()) {

				final var name = matcher.group(1).toUpperCase();
				return ComponentType.valueOf(name);
			}

		} catch (final Throwable ignored) {
		}

		return null;
	}

	/**
	 * Infer the component name from the channel name.
	 *
	 * @return the component name.
	 */
	public String inferComponentName() {

		try {

			final var matcher = COMPONENT_NAME_IN_CHANNEL_PATTERN.matcher(this.channelName);
			if (matcher.find()) {

				return matcher.group(1).toLowerCase().replaceAll("\\W+", "_");
			}

		} catch (final Throwable ignored) {
		}

		return null;

	}

}
