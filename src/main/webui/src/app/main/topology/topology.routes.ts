/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const TOPOLOGY_ROUTES: Routes = [
	{
		path: '',
		children: [
			{
				path: 'connections',
				loadChildren: () => import('./connections').then(m => m.TOPOLOGY_CONNECTIONS_ROUTES)
			},
			{
				path: 'editor',
				loadComponent: () => import('./editor').then(m => m.TopologyEditorComponent)
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'connections'
			},
			{
				path: '**',
				loadComponent: () => import('src/app/shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
