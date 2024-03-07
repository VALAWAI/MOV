/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * The components to manage the {@link Payload}s that are provided by the
 * events.
 *
 * @see Payload
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class PayloadService {

	/**
	 * The component to validate a {@code Payload}.
	 */
	@Inject
	Validator validator;

	/**
	 * Obtain the payload defined on a Json object and capture the exceptions.
	 *
	 * @param content to obtains the payload.
	 * @param type    of payload to obtain.
	 *
	 * @return the valid payload or {@code null} if it is not valid.
	 */
	public <T extends Payload> T safeDecodeAndVerify(JsonObject content, Class<T> type) {

		try {

			final var payload = content.mapTo(type);
			final var violations = this.validator.validate(payload);
			if (violations.isEmpty()) {

				return payload;

			} else {

				Log.errorv("The payload {0} is not valid because {1}", content, violations);
			}

		} catch (final Throwable error) {

			Log.errorv(error, "Could not obtain a payload from {0}", content);

		}

		return null;
	}

	/**
	 * Obtain the payload defined on a Json object or throws an exception if it .
	 *
	 * @param content to obtains the payload.
	 * @param type    of payload to obtain.
	 *
	 * @return the valid payload.
	 *
	 * @throws ConstraintViolationException if the content is not valid.
	 * @throws IllegalArgumentException     if the content is not encode the
	 *                                      required class.
	 */
	public <T extends Payload> T decodeAndVerify(JsonObject content, Class<T> type)
			throws ConstraintViolationException, IllegalArgumentException {

		final var payload = content.mapTo(type);
		final var violations = this.validator.validate(payload);
		if (violations.isEmpty()) {

			return payload;

		} else {

			throw new ConstraintViolationException("", violations);
		}
	}

}
