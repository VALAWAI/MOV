/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextObjectId;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Generic test for the classes that extends {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @param <T> type of model to test.
 *
 * @author VALAWAI
 */
public abstract class AbstractMinComponentTestCase<T extends MinComponent> extends ModelTestCase<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(T model) {

		model.id = nextObjectId();
		model.type = next(ComponentType.values());
		model.name = nextPattern("valaway/" + model.type.name().toLowerCase() + "_component_{0}");
		model.description = nextPattern("Description of component {0}");

	}

}
