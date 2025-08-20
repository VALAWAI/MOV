/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;

/**
 * Convert a message from a source to a message that can be managed by a target.
 *
 * @see TopologyConnectionEntity#targetMessageConverterJSCode
 *
 * @author VALAWAI
 */
public interface TargetMessageConverter {

}
