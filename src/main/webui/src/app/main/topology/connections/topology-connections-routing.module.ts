/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TopologyConnectionsComponent } from './topology-connections.component';

const routes: Routes = [
	{
		path: '',
		component: TopologyConnectionsComponent
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class TopologyConnectionsRoutingModule { }
