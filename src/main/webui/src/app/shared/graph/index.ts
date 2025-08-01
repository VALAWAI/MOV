/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { IPoint } from '@foblex/2d';

export { ComponentTypeBadgeComponent } from './component-type-badge.component';
export { ComponentTypeNodeContainerComponent } from './component-type-node-container.component';
export { ConnectionMarkersComponent } from './connection-markers.component';
export { DagreLayoutService } from './dagre-layout.service';
export { GraphModule } from './graph.module';
export { ToConnectionTypePipe } from './to-connection-type.pipe';
export { ToChannelNamePipe } from './to-channel-name.pipe';

/**
 * Calculate the distance between two points
 */
export function distance(source: IPoint, target: IPoint): number {

	var xDiff = source.x - target.x;
	var yDiff = source.y - target.y;
	return Math.sqrt(xDiff * xDiff + yDiff * yDiff);

}

/**
 * REturn the midle between two points.
 */
export function calculateMiddlePoint(point1: IPoint, point2: IPoint): IPoint {

	return {
		x: (point1.x + point2.x) / 2.0,
		y: (point1.y + point2.y) / 2.0
	};

}