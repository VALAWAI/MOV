/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
	{
		path: '',
		loadComponent: () => import('./app.component').then(c => c.AppComponent),
		children: [
			{ path: 'main', loadChildren: () => import('./main/main.module').then(m => m.MainModule) },
			{ path: '', redirectTo: 'main', pathMatch: 'full' },
			{ path: '**', loadChildren: () => import('./shared/not-found/not-found.module').then(m => m.NotFoundModule) }
		]
	}
];


@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
