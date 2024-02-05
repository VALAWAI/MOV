/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.valawai.mov;

import java.time.Instant;

/**
 * The component used to manage the time values.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface TimeManager {

	/**
	 * Return the current time.
	 *
	 * @return the seconds since the epoch of 1970-01-01T00:00:00Z.
	 */
	static long now() {

		return toTime(Instant.now());

	}

	/**
	 * Return the time associated to an instant.
	 *
	 * @param instant to convert too time.
	 *
	 * @return the seconds since the epoch of 1970-01-01T00:00:00Z.
	 */
	static long toTime(final Instant instant) {

		return Math.round(instant.toEpochMilli() / 1000.0);

	}

	/**
	 * Return the time associated to an instant.
	 *
	 * @param time in seconds since the epoch of 1970-01-01T00:00:00Z.
	 *
	 * @return the instant associated to the time.
	 */
	static Instant fromTime(final long time) {

		return Instant.ofEpochSecond(time);

	}

}
