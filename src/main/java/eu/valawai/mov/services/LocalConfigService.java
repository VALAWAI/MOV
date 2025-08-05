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

import org.eclipse.microprofile.config.ConfigProvider;

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
	 * @param value the value of the property.
	 *
	 * @return true if the property has been updated.
	 */
	public Uni<Boolean> setProperty(String key, String value) {

		return Uni.createFrom().item(() -> {

			try {

				this.properties.setProperty(key, value);
				System.setProperty(key, value);
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
	 * @param value the value of the property.
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
	 * @param key  the key of the property.
	 * @param type of the value.
	 *
	 * @return the value of the property or {@code null} if not defined or not match
	 *         the type.
	 */
	public <T> T getPropertyValue(String key, Class<T> type) {

		try {

			return ConfigProvider.getConfig().getValue(key, type);

		} catch (final Throwable cause) {

			Log.debugv(cause, "Cannot get the property {0}.", key);
			return null;

		}

	}

}
