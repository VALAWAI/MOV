/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
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
 * @author VALAWAI
 */
public abstract class ModelTestCase<T extends Model> extends ReflectionModelTestCase<T> {

}
