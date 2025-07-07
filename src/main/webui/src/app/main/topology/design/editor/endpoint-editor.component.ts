/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TopologyConnectionEndpoint } from '@app/shared/mov-api';
import { Subscription } from 'rxjs';
import { TopologyData } from './editor.models';
import { ChannleToNamePipe } from './channel-to-name.pipe';



@Component({
	standalone: true,
	selector: 'app-topology-connection-endpoint-editor',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatSelectModule,
		MatFormFieldModule,
		ChannleToNamePipe
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
	public topology: TopologyData | null = null;

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
			channel: new FormControl<string | null>(null, Validators.required)
		}
	);

	/**
	 * The subscription to the change to the node tag.
	 */
	private nodeTagChangedSubscription: Subscription | null = null;

	/**
	 * The subscription to the changes of the endpoint form.
	 */
	public endpointStatusSubscription: Subscription | null = null;

	/**
	 * The the last valid endpoint.
	 */
	private lastValid: TopologyConnectionEndpoint | null = null;

	/**
	 * The name of the posible channels for the	selected node.
	 */
	public posibleChannels: string[] = [];


	/**
	 * Register to the changes in the node.
	 */
	public ngOnInit() {

		this.endpointStatusSubscription = this.endpointForm.statusChanges.subscribe(
			{
				next: status => {

					if (status == 'VALID') {

						var newEndpoint = new TopologyConnectionEndpoint();
						newEndpoint.nodeTag = this.endpointForm.controls.nodeTag.value || null;
						newEndpoint.channel = this.endpointForm.controls.channel.value || null;
						if (JSON.stringify(this.lastValid) != JSON.stringify(newEndpoint)) {

							this.lastValid = newEndpoint;
							this.endpointUpdated.emit(newEndpoint);
						}
					}
				}
			}
		);


		this.nodeTagChangedSubscription = this.endpointForm.controls.nodeTag.valueChanges.subscribe(
			{
				next: () => this.updatePossibleChannels()
			}
		);
	}

	/**
	 * Register to the changes in the node.
	 */
	public ngOnDestroy() {

		if (this.endpointStatusSubscription != null) {

			this.endpointStatusSubscription.unsubscribe();
			this.endpointStatusSubscription = null;
		}

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
		this.updatePossibleChannels();

	}

	/**
	 * Define the posible channels that can be selected.
	 */
	private updatePossibleChannels() {

		this.posibleChannels = [];
		if (this.topology != null) {

			var node = this.topology.getNodeWithId(this.endpointForm.controls.nodeTag.value);
			if (node && node.model.component && node.model.component.channels) {

				for (var channel of node.model.component.channels) {

					if (channel.name && ((this.isSource && channel.publish != null) || (!this.isSource && channel.subscribe != null))) {

						this.posibleChannels.push(channel.name);
					}
				}

				this.posibleChannels.sort();
			}

		}

		if (this.endpointForm.controls.channel.value != null && this.posibleChannels.indexOf(this.endpointForm.controls.channel.value) < 0) {

			this.endpointForm.controls.channel.setErrors({ 'undefined': true });
		}

	}

}
