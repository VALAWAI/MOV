/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ComponentViewComponent } from './component-view.component';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { TimestampModule } from 'src/app/shared/timestamp';
import { LoadingModule } from 'src/app/shared/loading';
import {ComponentNameBeautifier} from './component-name-beautifierr.pipe';

@NgModule({
	declarations: [
		ComponentViewComponent,
		ComponentNameBeautifier
	],
	imports: [
		CommonModule,
		NgxJsonViewerModule,
		TimestampModule,
		LoadingModule
	],
	exports: [
		ComponentViewComponent,
		ComponentNameBeautifier
	]
})
export class ComponentViewModule { }
