/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainComponent } from './main.component';

const routes: Routes = [
	{
		path: '',
		component: MainComponent,
		children: [
			{
				path: 'status',
				loadChildren: () => import('./status/status.module').then(m => m.StatusModule)
			},
			{
				path: 'logs',
				loadChildren: () => import('./logs/logs.module').then(m => m.LogsModule)
			},
			{
				path: 'components/:id/show',
				loadChildren: () => import('./components/show/show-component.module').then(m => m.ShowComponentModule)
			},
			{
				path: 'components/:id/unregister',
				loadChildren: () => import('./components/unregister/unregister-component.module').then(m => m.UnregisterComponentModule)
			},
			{
				path: 'components/:id/connections',
				loadChildren: () => import('./components/connections/connections-component.module').then(m => m.ConnectionsComponentModule)
			},
			{
				path: 'components',
				loadChildren: () => import('./components/components.module').then(m => m.ComponentsModule)
			},
			{
				path: 'topology/connections/:id/show',
				loadChildren: () => import('./topology/connections/show/show-topology-connection.module').then(m => m.ShowTopologyConnectionModule)
			},
			{
				path: 'topology/connections',
				loadChildren: () => import('./topology/connections/topology-connections.module').then(m => m.TopologyConnectionsModule)
			},
			{
				path: 'topology',
				pathMatch: 'full',
				redirectTo: 'topology/connections'
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'status'
			},
			{
				path: '**',
				loadChildren: () => import('src/app/shared/not-found/not-found.module').then(m => m.NotFoundModule)
			}

		]
	}
];
@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class MainRoutingModule { }
