/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;

/**
 * The context used in the {@link MessageConverter} scripts.
 *
 * @see MessageConverter
 *
 * @author VALAWAI
 */
public class MessageConverterContext {

	/**
	 * The entity with the connection information.
	 */
	private final TopologyConnectionEntity entity;

	/**
	 * Add a log entry at error level.
	 *
	 * @param message the message, possibly with {0} for the connection id and {1}
	 *                for the parameter.
	 * @param param   the parameters to replace the {1} placeholder.
	 */
	public void error(String message, Object param) {

		Log.errorv(message, this.entity.toLogId(), param);
	}

	/**
	 * Add a log entry at warn level.
	 *
	 * @param message the message, possibly with {0} for the connection id and {1}
	 *                for the parameter.
	 * @param param   the parameters to replace the {1} placeholder.
	 */
	public void warn(String message, Object param) {

		Log.warnv(message, this.entity.toLogId(), param);
	}

	/**
	 * Add a log entry at info level.
	 *
	 * @param message the message, possibly with {0} for the connection id and {1}
	 *                for the parameter.
	 * @param param   the parameters to replace the {1} placeholder.
	 */
	public void info(String message, Object param) {

		Log.infov(message, this.entity.toLogId(), param);
	}

	/**
	 * Add a log entry at debug level.
	 *
	 * @param message the message, possibly with {0} for the connection id and {1}
	 *                for the parameter.
	 * @param param   the parameters to replace the {1} placeholder.
	 */
	public void debug(String message, Object param) {

		Log.debugv(message, this.entity.toLogId(), param);
	}

	/**
	 * Create a context for an entity.
	 *
	 * @param entity for the context.
	 */
	public MessageConverterContext(TopologyConnectionEntity entity) {

		this.entity = entity;
	}

	/**
	 * Return the identifier of the connection.
	 *
	 * @return the connection identifier.
	 */
	public String connectionId() {

		return this.entity.id.toHexString();
	}

}
