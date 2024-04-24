/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnregisterComponentRoutingModule } from './unregister-component-routing.module';
import { UnregisterComponentComponent } from './unregister-component.component';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingModule } from 'src/app/shared/loading';

@NgModule({
	declarations: [
		UnregisterComponentComponent,
	],
	imports: [
		CommonModule,
		UnregisterComponentRoutingModule,
		MatIconModule,
		MatButtonModule,
		LoadingModule
	],
	exports: [
	],
	providers: []
})
export class UnregisterComponentModule { }
