/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ComponentSelectorComponent } from './component-selector.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@NgModule({
	declarations: [
		ComponentSelectorComponent
	],
	imports: [
		CommonModule,
		MatAutocompleteModule,
		ReactiveFormsModule,
		MatInputModule,
		MatFormFieldModule
	],
	exports: [
		ComponentSelectorComponent
	]
})
export class ComponentSelectorModule { }
