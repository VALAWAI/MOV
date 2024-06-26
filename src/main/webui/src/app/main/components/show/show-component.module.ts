/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShowComponentRoutingModule } from './show-component-routing.module';
import { ShowComponentComponent } from './show-component.component';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingModule } from 'src/app/shared/loading';
import { ComponentViewModule } from 'src/app/shared/component/view';


@NgModule({
	declarations: [
		ShowComponentComponent,
	],
	imports: [
		CommonModule,
		ShowComponentRoutingModule,
		MatIconModule,
		MatButtonModule,
		LoadingModule,
		ComponentViewModule
	],
	exports: [
	],
	providers: []
})
export class ShowComponentModule { }
