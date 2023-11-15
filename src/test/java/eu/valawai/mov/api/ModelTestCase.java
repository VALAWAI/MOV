/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api;

import eu.valawai.mov.ReflectionModelTestCase;

/**
 * Test the classes that extends the {@link Model}.
 *
 * @see Model
 *
 * @param <T> type of model to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class ModelTestCase<T extends Model> extends ReflectionModelTestCase<T> {

}
