/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const DESIGN_ROUTES: Routes = [
	{
		path: '',
		children: [
			{
				path: 'editor',
				loadComponent: () => import('./editor').then(m => m.TopologyEditorComponent)
			},
			{
				path: 'search',
				loadComponent: () => import('./search').then(m => m.TopologySeakerComponent)
			},
			{
				path: 'components',
				loadComponent: () => import('./components').then(m => m.ComponentsLibraryComponent)
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'editor'
			},
			{
				path: '**',
				loadComponent: () => import('src/app/shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
