/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const COMPONENTS_ROUTES: Routes = [
	{
		path: '',
		children: [
			{
				path: 'register',
				loadChildren: () => import('./register').then(m => m.RegisterComponentComponent)
			},
			{
				path: ':id/show',
				loadChildren: () => import('./show').then(m => m.ShowComponentComponent)
			},
			{
				path: ':id/unregister',
				loadChildren: () => import('./unregister').then(m => m.UnregisterComponentComponent)
			},
			{
				path: 'search',
				loadChildren: () => import('./search').then(m => m.SearchComponent)
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
