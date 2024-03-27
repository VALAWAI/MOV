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
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TimestampModule } from 'src/app/shared/timestamp';
import { MatDialogModule } from '@angular/material/dialog';
import { ShowLogDialog } from './show-log.dialog';
import { NgxJsonViewerModule } from 'ngx-json-viewer';

@NgModule({
	declarations: [
		LogsComponent,
		ShowLogDialog
	],
	imports: [
		CommonModule,
		LogsRoutingModule,
		MatPaginatorModule,
		MatTableModule,
		MatFormFieldModule,
		MatInputModule,
		ReactiveFormsModule,
		MatIconModule,
		MatButtonModule,
		MatSelectModule,
		MatCheckboxModule,
		TimestampModule,
		MatDialogModule,
		NgxJsonViewerModule
	],
	exports: [
	],
	providers: []
})
export class LogsModule { }
