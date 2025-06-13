/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { TopologyViewNodeModel } from './topolofy-view-node.model';
import { MessageComponent } from '@app/shared/messages';



@Component({
	standalone: true,
	selector: 'app-topology-node-editor',
	imports: [
		CommonModule,
		MessageComponent
	],
	templateUrl: './node-editor.component.html'
})
export class TopologyNodeEditorComponent implements OnInit, OnDestroy {
	
	/**
	 * The node to edit.
	 */
	@Input()
	public node: TopologyViewNodeModel|null = null;

	/**
	 *  Create the component.
	 */
	constructor(
	) {

	}


	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {
	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {


	}

}
