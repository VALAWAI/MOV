/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
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
				path: 'components',
				loadChildren: () => import('./components/components.module').then(m => m.ComponentsModule)
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
