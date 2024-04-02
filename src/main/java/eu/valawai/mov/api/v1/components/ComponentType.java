/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * The possible types of components.
 *
 * @see Component
 *
 * @author VALAWAI
 */
@Schema(description = "The possible types of a VALAWAI component.")
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
