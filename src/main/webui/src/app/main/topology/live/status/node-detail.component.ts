/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StatusNode } from './status-node.model';
import { GraphModule } from '@app/shared/graph';


/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-node-detail',
	imports: [
		CommonModule,
		RouterModule, GraphModule
	],
	templateUrl: './node-detail.component.html'
})
export class NodeDetailComponent {

	/**
	 * The node to show the details of.
	 */
	@Input()
	public node: StatusNode | null = null;


}
