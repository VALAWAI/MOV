/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LogsComponent } from './logs.component';

const routes: Routes = [
	{
		path: '',
		component: LogsComponent
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class LogsRoutingModule { }
