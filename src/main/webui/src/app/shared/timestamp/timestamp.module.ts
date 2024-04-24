/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { TimestampPipe } from './timestamp.pipe';

@NgModule({
	declarations: [
		TimestampPipe
	],
	providers: [
		DatePipe
	],
	imports: [
		CommonModule,
	],
	exports: [
		TimestampPipe
	]

})
export class TimestampModule { }
