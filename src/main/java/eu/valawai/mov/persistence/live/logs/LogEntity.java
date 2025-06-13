/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.logs;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogLevel;
import eu.valawai.mov.api.v1.logs.LogRecord;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.common.jackson.ObjectIdSerializer;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

/**
 * The entity that contains the information of the log messages.
 *
 * @see LogRecord
 *
 * @author VALAWAI
 */
@MongoEntity(collection = LogEntity.COLLECTION_NAME)
public class LogEntity extends ReactivePanacheMongoEntity implements Serializable {

	/**
	 * The name of the collection with the logs.
	 */
	public static final String COLLECTION_NAME = "logs";

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The level of the log.
	 */
	public LogLevel level;

	/**
	 * The message of the log.
	 */
	public String message;

	/**
	 * The payload of the log.
	 */
	public String payload;

	/**
	 * The timestamp when the log has added.
	 */
	public long timestamp;

	/**
	 * The identifier of the component that has generated this log message.
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId componentId;

	/**
	 * Create a new log entity.
	 */
	public LogEntity() {

		this.timestamp = TimeManager.now();
	}

}
