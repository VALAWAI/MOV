/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainComponent } from './main.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';


@NgModule({
	declarations: [
		MainComponent,
	],
	imports: [
		CommonModule,
		MainRoutingModule,
		MatIconModule,
		MatButtonModule,
		MatMenuModule
	],
	exports: [
	],
	providers: []
})
export class MainModule { }
