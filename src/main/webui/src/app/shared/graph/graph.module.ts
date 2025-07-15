/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentTypeNodeContainerComponent } from './component-type-node-container.component';
import { ComponentTypeBadgeComponent } from './component-type-badge.component';
import { DagreLayoutService } from './dagre-layout.service';
import { ConnectionMarkersComponent } from './connection-markers.component';
import { ToConnectionTypePipe } from './to-connection-type.pipe';


@NgModule({
	imports: [
		CommonModule,
		ComponentTypeNodeContainerComponent,
		ComponentTypeBadgeComponent,
		ConnectionMarkersComponent,
		ToConnectionTypePipe
	],
	exports: [
		ComponentTypeNodeContainerComponent,
		ComponentTypeBadgeComponent,
		ConnectionMarkersComponent,
		ToConnectionTypePipe
	],
	providers: [
		DagreLayoutService
	]

})
export class GraphModule { }