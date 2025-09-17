/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.ConfigProvider;

import eu.valawai.mov.MOVConfiguration;
import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.api.v2.design.topologies.Topology;
import eu.valawai.mov.persistence.design.topology.GetTopology;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Change the local configuration of the MOV.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class LocalConfigService {

	/**
	 * The properties of the service.
	 */
	private final Properties properties;

	/**
	 * The path to the local configuration.
	 */
	private final Path path;

	/**
	 * Create the service.
	 */
	public LocalConfigService() {

		this.properties = new Properties();
		this.path = Path.of("config", "application.properties");
		if (this.path.toFile().exists()) {

			try {

				final var reader = Files.newBufferedReader(this.path);
				this.properties.load(reader);
				reader.close();

			} catch (final Throwable cause) {
				Log.errorv(cause, "Cannot get the local configuration.");
			}

		} else {

			try {

				this.path.getParent().toFile().mkdirs();

			} catch (final Throwable cause) {

				Log.errorv(cause, "Cannot create the local configuration directories.");
			}
		}

	}

	/**
	 * Update the local configuration.
	 *
	 * @param key   the key of the property.
	 * @param value the value of the property, or {@code null} to remove as local
	 *              property.
	 *
	 * @return true if the property has been updated.
	 */
	public Uni<Boolean> setProperty(String key, String value) {

		return Uni.createFrom().item(() -> {

			try {

				if (value == null) {

					this.properties.remove(key);
					System.clearProperty(key);

				} else {

					this.properties.setProperty(key, value);
					System.setProperty(key, value);
				}

				final var writer = Files.newBufferedWriter(this.path);
				this.properties.store(writer, "Local configuration of the MOV");
				writer.close();
				return true;

			} catch (final Throwable cause) {

				Log.errorv(cause, "Cannot update the local configuration.");
				return false;
			}

		});
	}

	/**
	 * Update the local configuration asynchronously and log the result.
	 *
	 * @param key   the key of the property.
	 * @param value the value of the property, or {@code null} to remove as local
	 *              property.
	 */
	public void setPropertyAsync(String key, String value) {

		this.setProperty(key, value).subscribe().with(result -> {

			if (result == false) {

				Log.errorv("Cannot update the property {0} to {1}.", key, value);

			} else {

				Log.debugv("Changed property {0} to {1}.", key, value);

			}
		});
	}

	/**
	 * Get a property value.
	 *
	 * @param key          the key of the property.
	 * @param type         of the value.
	 * @param defaultValue value if it not defined or can not get the value of the
	 *                     specified type.
	 *
	 * @return the value of the property or {@code null} if not defined or not match
	 *         the type.
	 */
	public <T> T getPropertyValue(String key, Class<T> type, T defaultValue) {

		try {

			final var value = ConfigProvider.getConfig().getOptionalValue(key, type);
			if (value.isPresent()) {

				return value.get();
			}

		} catch (final Throwable cause) {

			Log.debugv(cause, "Cannot get the property {0}.", key);
		}

		return defaultValue;

	}

	/**
	 * Get the {@link TopologyGraphEntity} to follow by the MOV.
	 *
	 * @return the designed topology to follow or {@code null} if does not have to
	 *         follow any topology.
	 */
	public Uni<TopologyGraphEntity> getTopologyGraphEntity() {

		final var id = this.getTopologyId();
		if (id != null) {

			return TopologyGraphEntity.findById(id);

		} else {

			return Uni.createFrom().nullItem();
		}
	}

	/**
	 * Get the {@link TopologyGraphEntity} to follow by the MOV.
	 *
	 * @return the designed topology to follow or {@code null} if does not have to
	 *         follow any topology.
	 */
	public ObjectId getTopologyId() {

		final var id = this.getPropertyValue(MOVConfiguration.TOPOLOGY_ID_NAME, String.class, null);
		if (id != null) {

			try {

				return new ObjectId(id);

			} catch (final Error error) {

				Log.errorv(error, "Cannot get the configured topology id from {0}.", id);
			}

		}

		return null;
	}

	/**
	 * Get the {@link Topology} to follow by the MOV.
	 *
	 * @return the designed topology to follow or {@code null} if does not have to
	 *         follow any topology.
	 */
	public Uni<Topology> getTopology() {

		final var id = this.getTopologyId();
		if (id != null) {

			return GetTopology.fresh().withId(id).execute();

		} else {

			return Uni.createFrom().nullItem();
		}
	}

	/**
	 * Get the {@link TopologyBehavior} defined in a property.
	 *
	 * @param name of the property to get its behaviour.
	 *
	 * @return the topology behaviour defined in a property.
	 */
	public TopologyBehavior getTopologyBehaviour(String name) {

		final var value = this.getPropertyValue(name, String.class, null);
		if (value != null) {

			try {

				return TopologyBehavior.valueOf(value);

			} catch (final Error error) {

				Log.errorv(error, "Cannot get the configured topology behaviour from {0}.", value);

			}

		}

		return TopologyBehavior.AUTO_DISCOVER;
	}

}
