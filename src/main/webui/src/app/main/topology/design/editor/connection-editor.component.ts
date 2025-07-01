/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';

import { MessageComponent } from '@app/shared/messages';
import { DesignTopologyConnection } from '@app/shared/mov-api';



@Component({
	standalone: true,
	selector: 'app-topology-connection-editor',
	imports: [
		CommonModule,
		MessageComponent
	],
	templateUrl: './connection-editor.component.html'
})
export class TopologyConnectionEditorComponent implements OnInit, OnDestroy {

	/**
	 * The connection to edit.
	 */
	@Input()
	public connection: DesignTopologyConnection | null = null;

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
