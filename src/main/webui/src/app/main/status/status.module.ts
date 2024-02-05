/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StatusRoutingModule } from './status-routing.module';
import { StatusComponent } from './status.component';


@NgModule({
	declarations: [
		StatusComponent,
	],
	imports: [
		CommonModule,
		StatusRoutingModule
	],
	exports: [
	],
	providers: []
})
export class StatusModule { }
