/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

import { MessageComponent } from '@app/shared/messages';
import { DesignTopologyConnection, Topology, TopologyConnectionEndpoint } from '@app/shared/mov-api';
import { Subscription } from 'rxjs';

function channelOnTagValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}


@Component({
	standalone: true,
	selector: 'app-topology-connection-endpoint-editor',
	imports: [
		CommonModule,
		MessageComponent,
		ReactiveFormsModule,
		MatSelectModule,
		MatFormFieldModule
	],
	templateUrl: './endpoint-editor.component.html'
})
export class TopologyConnectionEndpointEditorComponent implements OnInit, OnDestroy {

	/**
	 * This is {@code true} if the end point is a source.
	 */
	@Input()
	public isSource: boolean = true;

	/**
	 * The topology where the endpoint is defined.
	 */
	@Input()
	public topology: Topology | null = null;

	/**
	 * Notify when the endpoint is valid.
	 */
	@Output()
	public endpointUpdated = new EventEmitter<TopologyConnectionEndpoint>();

	/**
	 * The form to edit the endpoint.
	 */
	public endpointForm = new FormGroup(
		{
			nodeTag: new FormControl<string | null>(null, Validators.required),
			channel: new FormControl<string | null>(null, [Validators.required, channelOnTagValidator])
		}
	);

	/**
	 * The subscription to the change to the node tag.
	 */
	private nodeTagChangedSubscription: Subscription | null = null;

	/**
	 * Register to the changes in the node.
	 */
	public ngOnInit() {

		this.nodeTagChangedSubscription = this.endpointForm.controls.nodeTag.valueChanges.subscribe(
			{
				next: () => {
					//Change the posible channles and the current selcted channel

				}
			}
		);
	}

	/**
	 * Register to the changes in the node.
	 */
	public ngOnDestroy() {

		if (this.nodeTagChangedSubscription != null) {

			this.nodeTagChangedSubscription.unsubscribe();
			this.nodeTagChangedSubscription = null;
		}
	}



	/**
	 * Set the endpoint to edit.
	 */
	@Input()
	public set endpoint(endpoint: TopologyConnectionEndpoint | null | undefined) {

		this.endpointForm.patchValue(
			{
				nodeTag: endpoint?.nodeTag || null,
				channel: endpoint?.channel || null
			},
			{
				emitEvent: false
			}

		);



	}


}
