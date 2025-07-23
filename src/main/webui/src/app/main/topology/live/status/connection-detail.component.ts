/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AbstractConnectionDetailComponent } from './abstract-connection-detail.component';
import { GraphModule } from '@app/shared/graph';

@Component({
	standalone: true,
	selector: 'app-connection-detail',
	imports: [
		CommonModule,
		RouterModule,
		MatIconModule,
		GraphModule
	],
	templateUrl: './connection-detail.component.html'
})
export class ConnectionDetailComponent extends AbstractConnectionDetailComponent {


}
