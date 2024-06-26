/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShowTopologyConnectionRoutingModule } from './show-topology-connection-routing.module';
import { ShowTopologyConnectionComponent } from './show-topology-connection.component';

import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoadingModule } from 'src/app/shared/loading';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { TimestampModule } from 'src/app/shared/timestamp';


@NgModule({
	declarations: [
		ShowTopologyConnectionComponent,
	],
	imports: [
		CommonModule,
		ShowTopologyConnectionRoutingModule,
		MatIconModule,
		MatButtonModule,
		LoadingModule,
		NgxJsonViewerModule,
		TimestampModule
	],
	exports: [
	],
	providers: []
})
export class ShowTopologyConnectionModule { }
