/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.valawai.mov.persistence.AbstractEntityOperator;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

/**
 * The common infrastructure to run a test that uses the Master Of VALAWAI
 * (MOV).
 *
 * @author VALAWAI
 */
public class MasterOfValawaiTestCase {

	/**
	 * Check that a component is executed and return a non null value.
	 *
	 * @param operator to execute.
	 *
	 * @return the result of the execution.
	 */
	protected <T> T assertExecutionNotNull(AbstractEntityOperator<T, ?> operator) {

		return this.assertItemNotNull(operator.execute());
	}

	/**
	 * Check that a component is executed and return a non null value.
	 *
	 * @param operator to execute.
	 *
	 * @return the result of the execution.
	 */
	protected <T> T assertItemNotNull(Uni<T> operator) {

		final Function<? super Throwable, ? extends T> manageError = error -> {

			Log.errorv(error, "Cannot do the operation");
			return null;

		};
		final var result = operator.onFailure().recoverWithItem(manageError).await().atMost(Duration.ofSeconds(30));
		assertNotNull(result);
		return result;
	}

	/**
	 * Wait until the condition is satisfied.
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 * @param stepTime  time between steps.
	 * @param deadline  time that the wait has to be stopped.
	 */
	protected <T> T waitUntil(Supplier<T> supplier, Predicate<T> predicate, long stepTime, long deadline) {

		while (System.currentTimeMillis() < deadline) {

			final T value = supplier.get();
			if (predicate.test(value)) {

				return value;

			} else {
				try {
					Thread.sleep(stepTime);

				} catch (final InterruptedException ignored) {
				}
			}
		}

		fail("Deadline reached.");
		return null;

	}

	/**
	 * Wait until the condition is satisfied or a minutes passes.
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 */
	protected <T> T waitUntil(Supplier<T> supplier, Predicate<T> predicate) {

		return this.waitUntil(supplier, predicate, Duration.ofSeconds(1), Duration.ofMinutes(1));
	}

	/**
	 * Wait until the condition is satisfied or teh duration is reached..
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 * @param stepTime  time between steps.
	 * @param deadline  time that the wait has to be stopped.
	 */
	protected <T> T waitUntil(Supplier<T> supplier, Predicate<T> predicate, Duration stepTime, Duration deadline) {

		return this.waitUntil(supplier, predicate, Duration.ofSeconds(1).toMillis(),
				Duration.ofMinutes(1).toMillis() + System.currentTimeMillis());
	}

}
