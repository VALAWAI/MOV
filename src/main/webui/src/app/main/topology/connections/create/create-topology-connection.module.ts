/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingModule } from 'src/app/shared/loading';
import { CreateTopologyConnectionComponent } from './create-topology-connection.component';
import { CreateTopologyConnectionRoutingModule } from './create-topology-connection-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ComponentSelectorModule } from 'src/app/shared/component/selector';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';

@NgModule({
	declarations: [
		CreateTopologyConnectionComponent,
	],
	imports: [
		CommonModule,
		CreateTopologyConnectionRoutingModule,
		MatIconModule,
		MatButtonModule,
		LoadingModule,
		ReactiveFormsModule,
		MatInputModule,
		MatFormFieldModule,
		ComponentSelectorModule,
		MatAutocompleteModule,
		MatSelectModule,
		MatCheckboxModule
	],
	exports: [
	],
	providers: []
})
export class CreateTopologyConnectionModule { }
