/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RegisterComponentRoutingModule } from './register-component-routing.module';
import { RegisterComponentComponent } from './register-component.component';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingModule } from 'src/app/shared/loading';
import { ComponentViewModule } from 'src/app/shared/component/view';


@NgModule({
	declarations: [
		RegisterComponentComponent,
	],
	imports: [
		CommonModule,
		RegisterComponentRoutingModule,
		MatIconModule,
		MatButtonModule,
		LoadingModule,
		ComponentViewModule
	],
	exports: [
	],
	providers: []
})
export class RegisterComponentModule { }
