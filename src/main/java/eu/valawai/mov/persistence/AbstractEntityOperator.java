/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import io.smallrye.mutiny.Uni;

/**
 * A generic operation over an entity.
 *
 * @param <R> type of values to return.
 * @param <O> type of operator.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractEntityOperator<R, O extends AbstractEntityOperator<R, O>> {

	/**
	 * Return the current operator.
	 *
	 * @return this operator.
	 */
	@SuppressWarnings("unchecked")
	protected O operator() {

		return (O) this;

	}

	/**
	 * Execute the operation.
	 *
	 * @return the future with the result of the operator.
	 */
	public abstract Uni<R> execute();

}