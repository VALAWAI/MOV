/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.valawai.mov.ReflectionModel;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A payload of any message that is interchange though a channel.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@JsonInclude(Include.NON_EMPTY)
@RegisterForReflection
public class Payload extends ReflectionModel {

}
