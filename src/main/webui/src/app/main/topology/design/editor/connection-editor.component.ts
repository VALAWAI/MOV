/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { MessageComponent } from '@app/shared/messages';
import { DesignTopologyConnection, Topology, TopologyConnectionEndpoint } from '@app/shared/mov-api';
import { TopologyConnectionEndpointEditorComponent } from './endpoint-editor.component';



@Component({
	standalone: true,
	selector: 'app-topology-connection-editor',
	imports: [
		CommonModule,
		MessageComponent,
		ReactiveFormsModule,
		TopologyConnectionEndpointEditorComponent
	],
	templateUrl: './connection-editor.component.html'
})
export class TopologyConnectionEditorComponent implements OnInit, OnDestroy {

	/**
	 * The topology where the connection is defined.
	 */
	@Input()
	public topology: Topology | null = null;

	/**
	 * Notify when teh node has been updated.
	 */
	@Output()
	public connectionUpdated = new EventEmitter<DesignTopologyConnection>();

	/**
	 * The form to edit the connection.
	 */
	public connectionForm = new FormGroup(
		{
			source: new FormControl<TopologyConnectionEndpoint | null>(null),
			target: new FormControl<TopologyConnectionEndpoint | null>(null),
			convertCode: new FormControl<string | null>(null)
		}
	);


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

	/**
	 * Set the conneciton to edit.
	 */
	@Input()
	public set connection(connection: DesignTopologyConnection | null | undefined) {

		this.connectionForm.patchValue(
			{
				source: connection?.source || null,
				target: connection?.target || null,
				convertCode: connection?.convertCode || null
			},
			{
				emitEvent: false
			}

		);

	}


}
