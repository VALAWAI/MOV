/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.topology;

import org.bson.types.ObjectId;

import eu.valawai.mov.persistence.AbstractEntityOperator;

/**
 * Abstract operation over a {@link TopologyConnectionEntity}.
 *
 * @param <R> result of the operation.
 * @param <O> operation to do.
 *
 * @see TopologyConnectionEntity
 *
 * @author VALAWAI
 */
public abstract class AbstractTopologyConnectionOperation<R, O extends AbstractTopologyConnectionOperation<R, O>>
		extends AbstractEntityOperator<R, O> {

	/**
	 * The identifier of the connection to do the operation.
	 */
	protected ObjectId connectionId;

	/**
	 * Set the identifier of the connection to be used by this operator.
	 *
	 * @param connectionId identifier of the connection associated to this operator.
	 *
	 * @return this operator.
	 */
	public O withConnection(ObjectId connectionId) {

		this.connectionId = connectionId;
		return this.operator();
	}

}
