/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const TOPOLOGY_CONNECTIONS_ROUTES: Routes = [
	{
		path: '',
		children: [
			{
				path: ':id/show',
				loadComponent: () => import('./show').then(m => m.ShowTopologyConnectionComponent)
			},
			{
				path: ':id/change',
				loadComponent: () => import('./change').then(m => m.ChangeTopologyConnectionComponent)
			},
			{
				path: 'create',
				loadComponent: () => import('./create').then(m => m.CreateTopologyConnectionComponent)
			},
			{
				path: 'search',
				loadComponent: () => import('./search').then(m => m.TopologyConnectionsSearchComponent)
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'search'
			},
			{
				path: '**',
				loadComponent: () => import('src/app/shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
