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
				path: 'live',
				loadChildren: () => import('./live').then(m => m.LIVE_ROUTES)
			},
			{
				path: 'design',
				loadChildren: () => import('./design').then(m => m.DESIGN_ROUTES)
			},
			{
				path: '',
				pathMatch: 'full',
				redirectTo: 'live'
			},
			{
				path: '**',
				loadComponent: () => import('@shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
