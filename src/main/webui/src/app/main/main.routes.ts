/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const MAIN_ROUTES: Routes = [
	{
		path: '',
		loadComponent: () => import('./main.component').then(c => c.MainComponent),
		children: [
			{
				path: 'status',
				loadChildren: () => import('./status').then(m => m.StatusComponent)
			},
			{
				path: 'logs',
				loadChildren: () => import('./logs').then(m => m.LogsComponent)
			},
			{
				path: 'components',
				loadChildren: () => import('./components').then(m => m.COMPONENTS_ROUTES)
			},
			{
				path: 'topology/connections',
				loadChildren: () => import('./topology/connections').then(m => m.TOPOLOGY_CONNECTIONS_ROUTES)
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'status'
			},
			{
				path: '**',
				loadComponent: () => import('src/app/shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
