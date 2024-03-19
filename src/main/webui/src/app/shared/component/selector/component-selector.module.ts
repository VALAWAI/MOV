/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ComponentSelectorComponent } from './component-selector.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
	declarations: [
		ComponentSelectorComponent
	],
	imports: [
		CommonModule,
		MatProgressSpinnerModule
	],
	exports: [
		ComponentSelectorComponent
	]
})
export class ComponentSelectorModule { }
