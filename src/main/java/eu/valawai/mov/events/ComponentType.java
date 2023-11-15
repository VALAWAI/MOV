/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

/**
 * The possible types of components.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum ComponentType {

	/**
	 * The sensors or actuators over the environment.
	 */
	C0,

	/**
	 * The components that make decisions.
	 */
	C1,

	/**
	 * The components that manage the value awareness.
	 */
	C2;

}
