/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import eu.valawai.mov.api.v1.logs.LogRecord;
import eu.valawai.mov.persistence.LogRecordRepository;
import jakarta.inject.Inject;

/**
 * The common infrastructure to run a test that uses the Master Of VALAWAI
 * (MOV).
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MasterOfValawaiTestCase {

	/**
	 * The repository with the logs.
	 */
	@Inject
	protected LogRecordRepository logs;

	/**
	 * Wait until received a log.
	 *
	 * @param index    number of logs to wait.
	 * @param duration maximum time to wait.
	 *
	 * @return the last log.
	 *
	 * @throws AssertionError if reached timeout.
	 */
	protected LogRecord wainUntilLog(int index, Duration duration) {

		final var deadline = System.currentTimeMillis() + duration.toMillis();
		do {

			try {

				Thread.sleep(1000);

			} catch (final Throwable ignored) {
			}

			if (System.currentTimeMillis() > deadline) {

				fail("Timeout reached when wait for a log..");
			}

		} while (this.logs.count() < index);

		return this.logs.last();
	}

}
