/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MessageComponent } from '@app/shared/messages';
import { DesignTopologyConnection, Topology, TopologyConnectionEndpoint, TopologyGraphConnectionType } from '@app/shared/mov-api';
import { TopologyConnectionEndpointEditorComponent } from './endpoint-editor.component';
import { TopologyData } from './editor.models';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { EFConnectionType } from '@foblex/flow';
import { Subscription } from 'rxjs';



@Component({
	standalone: true,
	selector: 'app-topology-connection-editor',
	imports: [
		CommonModule,
		MessageComponent,
		ReactiveFormsModule,
		TopologyConnectionEndpointEditorComponent,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule
	],
	templateUrl: './connection-editor.component.html'
})
export class TopologyConnectionEditorComponent implements OnInit, OnDestroy {

	/**
	 * The topology where the connection is defined.
	 */
	@Input()
	public topology: TopologyData | null = null;

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
			convertCode: new FormControl<string | null>(null),
			type: new FormControl<TopologyGraphConnectionType | null>(null, Validators.required)
		}
	);

	/**
	 * The subscription to the changes of the connection form.
	 */
	public connectionStatusSubscription: Subscription | null = null;

	/**
	 * The the last valid connection.
	 */
	private lastValid: DesignTopologyConnection | null = null;

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.connectionStatusSubscription = this.connectionForm.statusChanges.subscribe(
			{
				next: status => {

					if (status == 'VALID') {

						var newConnection = new DesignTopologyConnection();
						newConnection.source = this.connectionForm.controls.source.value || null;
						newConnection.target = this.connectionForm.controls.target.value || null;
						newConnection.convertCode = this.connectionForm.controls.convertCode.value || null;
						newConnection.type = this.connectionForm.controls.type.value || null;

						if (JSON.stringify(this.lastValid) != JSON.stringify(newConnection)) {

							this.lastValid = newConnection;
							this.connectionUpdated.emit(newConnection);
						}
					}
				}
			}
		);

	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {

		if (this.connectionStatusSubscription != null) {

			this.connectionStatusSubscription.unsubscribe();
			this.connectionStatusSubscription = null;
		}


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
				convertCode: connection?.convertCode || null,
				type: connection?.type || null
			},
			{
				emitEvent: false
			}
		);

	}


}
