/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.function.Function;

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
	 * @reutnr the result of the execution.
	 */
	protected <T> T assertExecutionNotNull(AbstractEntityOperator<T, ?> operator) {

		return this.assertItemNotNull(operator.execute());
	}

	/**
	 * Check that a component is executed and return a non null value.
	 *
	 * @param operator to execute.
	 *
	 * @reutnr the result of the execution.
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

}
