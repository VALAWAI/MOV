/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.live.components;

import org.bson.types.ObjectId;

import eu.valawai.mov.persistence.AbstractEntityOperator;

/**
 * Abstract operation over a {@link ComponentEntity}.
 *
 * @param <R> result of the operation.
 * @param <O> operation to do.
 *
 * @see ComponentEntity
 *
 * @author VALAWAI
 */
public abstract class AbstractComponentOperation<R, O extends AbstractComponentOperation<R, O>>
		extends AbstractEntityOperator<R, O> {

	/**
	 * The identifier of the component to do the operation.
	 */
	protected ObjectId componentId;

	/**
	 * Set the identifier of the component to be used by this operator.
	 *
	 * @param componentId identifier of the component associated to this operator.
	 *
	 * @return this operator.
	 */
	public O withComponent(ObjectId componentId) {

		this.componentId = componentId;
		return this.operator();
	}

}
