/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import eu.valawai.mov.api.Model;

/**
 * Represents a specific point in a two-dimensional Cartesian coordinate system.
 * This class is designed as an immutable value object, typically used for
 * defining positions, coordinates, or graphical placements within a 2D space.
 *
 * @author VALAWAI
 */
@Schema(title = "A point in the 2D graph.", description = "Represents a point with X and Y coordinates in a 2D graph.")
public class Point extends Model {

	/**
	 * The X-coordinate (horizontal position) of the point in the 2D graph.
	 */
	@Schema(description = "The X-coordinate (horizontal position) of the point.", example = "100.5")
	public double x;

	/**
	 * The Y-coordinate (vertical position) of the point in the 2D graph.
	 */
	@Schema(description = "The Y-coordinate (vertical position) of the point.", example = "250.0")
	public double y;
}
