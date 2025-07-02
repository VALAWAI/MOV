/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const COMPONENTS_LIBRARY_ROUTES: Routes = [
	{
		path: '',
		children: [
			{
				path: 'update',
				loadComponent: () => import('./update').then(m => m.UpdateLibraryComponent)
			},
			{
				path: ':id/show',
				loadComponent: () => import('./show').then(m => m.ShowLibraryComponent)
			},
			{
				path: 'search',
				loadComponent: () => import('./search').then(m => m.SearchLibraryComponent)
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
