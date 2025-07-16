/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

/**
 * Generic query operator that paginate its query.
 *
 * @param <T> type of page that it obtains.
 * @param <O> type of the operator.
 *
 * @author VALAWAI
 */
public abstract class AbstractPaginatedQuery<T, O extends AbstractPaginatedQuery<T, O>>
		extends AbstractEntityOperator<T, O> {

	/**
	 * The offset to the first model to return.
	 */
	protected int offset = 0;

	/**
	 * The number maximum of models to return.
	 */
	protected int limit = 10;

	/**
	 * The index of the first element to return.
	 *
	 * @param offset of the first element to return.
	 *
	 * @return this operator.
	 */
	public O withOffset(final int offset) {

		this.offset = offset;
		return this.operator();
	}

	/**
	 * The number maximum of elements in the page to return.
	 *
	 * @param limit of size of the page elements to return.
	 *
	 * @return this operator.
	 */
	public O withLimit(final int limit) {

		this.limit = limit;
		return this.operator();
	}
}
