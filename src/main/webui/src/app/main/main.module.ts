/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainComponent } from './main.component';


@NgModule({
	declarations: [
		MainComponent,
	],
	imports: [
		CommonModule,
		MainRoutingModule
	],
	exports: [
	],
	providers: []
})
export class MainModule { }
