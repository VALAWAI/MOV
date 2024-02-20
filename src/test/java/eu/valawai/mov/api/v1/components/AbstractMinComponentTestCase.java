/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Generic test for the classes that extends {@link MinComponent}.
 *
 * @see MinComponent
 *
 * @param <T> type of model to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractMinComponentTestCase<T extends MinComponent> extends ModelTestCase<T> {

}