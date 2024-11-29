/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Routes } from '@angular/router';

export const APP_ROUTES: Routes = [
	{
		path: '',
		loadComponent: () => import('./app.component').then(c => c.AppComponent),
		children: [
			{
				path: 'main',
				loadChildren: () => import('./main/main.routes').then(m => m.MAIN_ROUTES)
			},
			{
				path: '',
				redirectTo: 'main',
				pathMatch: 'full'
			},
			{
				path: '**',
				loadComponent: () => import('./shared/not-found').then(m => m.NotFoundComponent)
			}
		]
	}
];
