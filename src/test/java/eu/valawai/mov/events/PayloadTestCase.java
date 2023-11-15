/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import eu.valawai.mov.ReflectionModelTestCase;

/**
 * Generic test over the classes that extends the {@link Payload}.
 *
 * @see Payload
 *
 * @param <P> type of payload to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class PayloadTestCase<P extends Payload> extends ReflectionModelTestCase<P> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public P createEmptyModel() {

		return this.createEmptyPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public P nextModel() {

		return this.nextPayload();
	}

	/**
	 * Create a new empty payload.
	 *
	 * @return the created empty payload.
	 */
	public abstract P createEmptyPayload();

	/**
	 * Create a new payload.
	 *
	 * @return the created a random next payload.
	 */
	public abstract P nextPayload();

}
