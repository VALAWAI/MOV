/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TopologyConnectionsRoutingModule } from './topology-connections-routing.module';
import { TopologyConnectionsComponent } from './topology-connections.component';

import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { LoadingModule } from 'src/app/shared/loading';

@NgModule({
	declarations: [
		TopologyConnectionsComponent,
	],
	imports: [
		CommonModule,
		TopologyConnectionsRoutingModule,
		MatPaginatorModule,
		MatTableModule,
		MatFormFieldModule,
		MatInputModule,
		ReactiveFormsModule,
		MatIconModule,
		MatButtonModule,
		MatSelectModule,
		MatCheckboxModule,
		MatMenuModule,
		LoadingModule
	],
	exports: [
	],
	providers: []
})
export class TopologyConnectionsModule { }
