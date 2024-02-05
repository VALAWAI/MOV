/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LogsRoutingModule } from './logs-routing.module';
import { LogsComponent } from './logs.component';


@NgModule({
	declarations: [
		LogsComponent,
	],
	imports: [
		CommonModule,
		LogsRoutingModule
	],
	exports: [
	],
	providers: []
})
export class LogsModule { }
