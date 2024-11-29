/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.valawai.mov.events.RabbitMQTestResource;
import eu.valawai.mov.persistence.AbstractEntityOperator;
import eu.valawai.mov.persistence.MongoTestResource;
import eu.valawai.mov.persistence.logs.LogEntity;
import io.quarkus.logging.Log;
import io.quarkus.test.common.WithTestResource;
import io.smallrye.mutiny.Uni;

/**
 * The common infrastructure to run a test that uses the Master Of VALAWAI
 * (MOV).
 *
 * @author VALAWAI
 */
@WithTestResource(MongoTestResource.class)
@WithTestResource(RabbitMQTestResource.class)
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
	 * Check that a component is executed and return a null value.
	 *
	 * @param operator to execute.
	 */
	protected <T> void assertExecutionNull(AbstractEntityOperator<T, ?> operator) {

		this.assertItemNull(operator.execute());
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
	 * Check that a component is executed and return a null value.
	 *
	 * @param operator to execute.
	 */
	protected <T> void assertItemNull(Uni<T> operator) {

		final List<Throwable> errors = new ArrayList<>();
		final Function<? super Throwable, ? extends T> manageError = error -> {

			Log.errorv(error, "Cannot do the operation");
			errors.add(error);
			return null;

		};
		final var result = operator.onFailure().recoverWithItem(manageError).await().atMost(Duration.ofSeconds(30));
		assertNull(result);
		assertTrue(errors.isEmpty(), "The operator has failed");
	}

	/**
	 * Check that a component is executed and return a null value or it has failed.
	 *
	 * @param operator to execute.
	 */
	protected <T> void assertItemNullOrFailed(Uni<T> operator) {

		final var result = operator.onFailure().recoverWithNull().await().atMost(Duration.ofSeconds(30));
		assertNull(result);
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
	 *
	 * @see #waitUntilNotNull(Supplier, Predicate, Duration, Duration)
	 */
	protected <T> T waitUntil(Supplier<T> supplier, Predicate<T> predicate) {

		return this.waitUntil(supplier, predicate, Duration.ofSeconds(1), Duration.ofMinutes(1));
	}

	/**
	 * Wait until the condition is satisfied or the duration is reached..
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 * @param stepTime  time between steps.
	 * @param deadline  time that the wait has to be stopped.
	 *
	 * @see #waitUntilNotNull(Supplier, Predicate, long, long)
	 */
	protected <T> T waitUntil(Supplier<T> supplier, Predicate<T> predicate, Duration stepTime, Duration deadline) {

		return this.waitUntil(supplier, predicate, Duration.ofSeconds(1).toMillis(),
				Duration.ofMinutes(1).toMillis() + System.currentTimeMillis());
	}

	/**
	 * Wait until the value is not null and the condition is satisfied.
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 * @param stepTime  time between steps.
	 * @param deadline  time that the wait has to be stopped.
	 *
	 * @see #assertItemNotNull(Uni)
	 */
	protected <T> T waitUntilNotNull(Supplier<Uni<T>> supplier, Predicate<T> predicate, long stepTime, long deadline) {

		while (System.currentTimeMillis() < deadline) {

			final T value = this.assertItemNotNull(supplier.get());
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
	 * Wait until the value is not null and the condition is satisfied or a minutes
	 * passes.
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 *
	 * @see #waitUntilNotNull(Supplier, Predicate, Duration, Duration)
	 */
	protected <T> T waitUntilNotNull(Supplier<Uni<T>> supplier, Predicate<T> predicate) {

		return this.waitUntilNotNull(supplier, predicate, Duration.ofSeconds(1), Duration.ofMinutes(1));
	}

	/**
	 * Wait until the value is not null and the condition is satisfied or the
	 * duration is reached..
	 *
	 * @param supplier  to provide the value to check.
	 * @param predicate to be true.
	 * @param stepTime  time between steps.
	 * @param deadline  time that the wait has to be stopped.
	 *
	 * @see #waitUntilNotNull(Supplier, Predicate, long, long)
	 */
	protected <T> T waitUntilNotNull(Supplier<Uni<T>> supplier, Predicate<T> predicate, Duration stepTime,
			Duration deadline) {

		return this.waitUntilNotNull(supplier, predicate, Duration.ofSeconds(1).toMillis(),
				Duration.ofMinutes(1).toMillis() + System.currentTimeMillis());
	}

	/**
	 * Do an action and wait at maximum one minute that a new log message is stored.
	 *
	 * @param action to do.
	 *
	 * @see #executeAndWaitUntilNewLog(Runnable, Duration)
	 */
	protected void executeAndWaitUntilNewLog(Runnable action) {

		this.executeAndWaitUntilNewLog(action, Duration.ofMinutes(1));
	}

	/**
	 * Do an action and wait until a new log message is stored or the deadline
	 * period has passed.
	 *
	 * @param action   to do.
	 * @param deadline maximum time to wait that the action is done.
	 */
	protected void executeAndWaitUntilNewLog(Runnable action, Duration deadline) {

		this.executeAndWaitUntilNewLogs(1, action, deadline);

	}

	/**
	 * Do an action and wait until a new log message is stored or the deadline
	 * period has passed.
	 *
	 * @param num    of log to wait until happens the specified number of logs.
	 * @param action to do.
	 */
	protected void executeAndWaitUntilNewLogs(int num, Runnable action) {

		this.executeAndWaitUntilNewLogs(num, action, Duration.ofMinutes(1));
	}

	/**
	 * Do an action and wait until a new log message is stored or the deadline
	 * period has passed.
	 *
	 * @param num      of log to wait until happens the specified number of logs.
	 * @param action   to do.
	 * @param deadline maximum time to wait that the action is done.
	 */
	protected void executeAndWaitUntilNewLogs(int num, Runnable action, Duration deadline) {

		final var now = TimeManager.now();
		final var second = Duration.ofSeconds(1);
		try {

			Thread.sleep(second.toMillis());
			action.run();

		} catch (final AssertionError cause) {

			throw cause;

		} catch (final Throwable error) {

			error.printStackTrace();
			fail("Cannot execute the action");
		}
		this.waitUntilNotNull(() -> LogEntity.count("timestamp > ?1", now), c -> c >= num, second, deadline);

	}

	/**
	 * Check that a Uni fails.
	 *
	 * @param uni      to check.
	 * @param duration to wait that the uni fails.
	 *
	 * @return the error that happened.
	 */
	protected Throwable assertFailure(Uni<?> uni, Duration duration) {

		final List<Throwable> errors = new ArrayList<>();
		final var result = uni.onFailure().recoverWithItem(error -> {
			errors.add(error);
			return null;
		}).await().atMost(duration);

		assertNull(result, "The uni has returned a result without fail.");
		assertEquals(1, errors.size(), "Not get the failed error");
		return errors.get(0);

	}

	/**
	 * Check that a Uni fails at most in 30 seconds.
	 *
	 * @param uni to check.
	 *
	 * @return the error that happened.
	 */
	protected Throwable assertFailure(Uni<?> uni) {

		return this.assertFailure(uni, Duration.ofSeconds(30));
	}

	/**
	 * Check that the execution of the uni is a success and return a null value.
	 *
	 * @param uni      to check.
	 * @param duration to wait that the uni fails.
	 */
	protected void assertItemIsNull(Uni<?> uni, Duration duration) {

		final List<Throwable> errors = new ArrayList<>();
		final var result = uni.onFailure().recoverWithItem(error -> {
			errors.add(error);
			return null;
		}).await().atMost(duration);

		assertNull(result, "The uni has returned a result .");
		if (!errors.isEmpty()) {

			fail(errors.get(0));
		}
	}

	/**
	 * Check that the execution of the uni is a success and return a null value in
	 * at least 30 seconds.
	 *
	 * @param uni to check.
	 */
	protected void assertItemIsNull(Uni<?> uni) {

		this.assertItemIsNull(uni, Duration.ofSeconds(30));
	}

	/**
	 * Load a resource as string.
	 *
	 * @param resource to load.
	 *
	 * @retuen the string value of the resource.
	 */
	protected String loadResourceAsString(String resource) {

		try {

			final var loader = this.getClass().getClassLoader();
			final var stream = loader.getResourceAsStream(resource);
			final var bytes = stream.readAllBytes();
			return new String(bytes);

		} catch (final Throwable error) {

			error.printStackTrace();
			fail("Cannot load the resource " + resource);
			return null;
		}

	}
}
