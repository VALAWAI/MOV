/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

/**
 * Generic operation used to obtain a page of something.
 *
 * @param <T> type of page that it obtains.
 * @param <O> type of the operator.
 *
 * @author VALAWAI
 */
public abstract class AbstractGetPage<T, O extends AbstractGetPage<T, O>> extends AbstractGetMinPage<T, O> {

	/**
	 * The pattern to match the model has returned.
	 */
	protected String pattern;

	/**
	 * Create a new get page operator.
	 *
	 * @param fieldName field name to store the models of the page.
	 */
	protected AbstractGetPage(String fieldName) {

		super(fieldName);

	}

	/**
	 * The pattern to match the page elements.
	 *
	 * @param pattern to match the elements to return.
	 *
	 * @return this operator.
	 */
	public O withPattern(final String pattern) {

		this.pattern = pattern;
		return this.operator();
	}

}
