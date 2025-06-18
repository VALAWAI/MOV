/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from "@foblex/2d";



/**
 *  Represents a specific point in a two-dimensional Cartesian coordinate system.
 * This class is designed as an immutable value object, typically used for
 * defining positions, coordinates, or graphical placements within a 2D space.
 *
 * @author VALAWAI
 */
export class Point implements IPoint {

	/**
	 * The X-coordinate (horizontal position) of the point in the 2D graph.
	 */
	public x: number = 0;

	/**
	 * The Y-coordinate (vertical position) of the point in the 2D graph.
	 */
	public y: number = 0;

}
