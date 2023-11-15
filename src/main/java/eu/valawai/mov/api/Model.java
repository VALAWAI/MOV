/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.valawai.mov.ReflectionModel;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A data model that is interchanged on an API.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@JsonInclude(Include.NON_EMPTY)
@RegisterForReflection
public class Model extends ReflectionModel {

}
