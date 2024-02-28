/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.valawai.mov.ReflectionModel;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * A data model that is interchanged on an API.
 *
 * @author VALAWAI
 */
@JsonInclude(Include.NON_EMPTY)
@RegisterForReflection
public class Model extends ReflectionModel {

}
