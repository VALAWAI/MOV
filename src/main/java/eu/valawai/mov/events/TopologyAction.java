/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

/**
 * The type of action to do over the topology.
 *
 * @author VALAWAI
 */
public enum TopologyAction {

	/**
	 * Enable the connection between components.
	 */
	ENABLE,

	/**
	 * Disable the connection between components.
	 */
	DISABLE;

}
