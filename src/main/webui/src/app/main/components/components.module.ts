/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ComponentsRoutingModule } from './components-routing.module';
import { ComponentsComponent } from './components.component';


@NgModule({
	declarations: [
		ComponentsComponent,
	],
	imports: [
		CommonModule,
		ComponentsRoutingModule
	],
	exports: [
	],
	providers: []
})
export class ComponentsModule { }
