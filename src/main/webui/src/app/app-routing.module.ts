/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';

const routes: Routes = [
	{
		path: '',
		component: AppComponent,
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
