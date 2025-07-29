/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ChannelSchema, } from '@app/shared/mov-api';
import { Subscription } from 'rxjs';
import { EditorTopologyService } from './editor-topology.service';
import { EditorNode } from './editor-node.model';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { EditorEndpoint } from './editor-endpoint.model';
import { MatInputModule } from '@angular/material/input';
import { toChannelName, toRegexp } from '@app/shared';
import { GraphModule } from '@app/shared/graph';



export function requiredNode(): ValidatorFn {

	return (control: AbstractControl): ValidationErrors | null => {

		if (control != null && (control.value == null || typeof control.value === 'string')) {

			return { 'required': true };

		} else {

			return null;
		}
	};
}

export function requiredChannel(editor: EndpointEditorComponent): ValidatorFn {

	return (control: AbstractControl): ValidationErrors | null => {

		if (control != null &&
			control.value === null ||
			(typeof control.value === 'string' &&
				editor.possibleChannels.findIndex(c => c.name === control.value) < 0)
			|| (
				typeof control.value === 'object' &&
				editor.possibleChannels.findIndex(c => c.name === control.value.name) < 0
			)
		) {

			return { 'required': true };


		} else {
			return null;
		}

	};
}

@Component({
	standalone: true,
	selector: 'app-topology-endpoint-editor',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatAutocompleteModule,
		GraphModule
	],
	templateUrl: './endpoint-editor.component.html'
})
export class EndpointEditorComponent implements OnInit, OnDestroy {

	/**
	 * This is {@code true} if the end point is a source.
	 */
	@Input()
	public isSource: boolean = true;

	/**
	 * The topology where the endpoint is defined.
	 */
	public readonly topology = inject(EditorTopologyService);

	/**
	 * Notify when the endpoint is valid.
	 */
	@Output()
	public endpointUpdated = new EventEmitter<EditorEndpoint>();

	/**
	 * The form to edit the endpoint.
	 */
	public endpointForm = new FormGroup(
		{
			node: new FormControl<EditorNode | string | null>(null, {
				validators: requiredNode(),
				updateOn: 'change'
			}),
			channel: new FormControl<ChannelSchema | string | null>(null, {
				validators: requiredChannel(this),
				updateOn: 'change'
			}),
		}
	);

	/**
	 * The filtered nodes.
	 */
	public possibleNodes: EditorNode[] = [];

	/**
	 * The filtered nodes.
	 */
	public possibleChannels: ChannelSchema[] = [];

	/**
	 * The subscription to the change to the node tag.
	 */
	private subscruptions: Subscription[] = [];

	/**
	 * The the last valid endpoint.
	 */
	private lastValid: EditorEndpoint | null = null;


	/**
	 * Register to the changes in the node.
	 */
	public ngOnInit() {

		this.updatePossibleNodes();
		this.subscruptions.push(

			this.endpointForm.controls.node.valueChanges.subscribe(
				{
					next:
						value => {

							this.updatePossibleNodes(value);
							if (typeof value === 'object') {

								this.endpointForm.controls.channel.enable();
								this.updatePossibleChannels();
								this.endpointForm.controls.channel.updateValueAndValidity();
							}
						}
				}
			)
		);
		this.subscruptions.push(

			this.endpointForm.controls.channel.valueChanges.subscribe(
				{
					next: value => this.updatePossibleChannels(value)
				}
			)
		);

		this.subscruptions.push(
			this.endpointForm.statusChanges.subscribe(
				{
					next: status => {

						if (status == 'VALID') {

							var value = this.endpointForm.value;
							var node = value.node as EditorNode;
							var channel = value.channel as ChannelSchema;
							var newEndpoint = new EditorEndpoint(node.id, channel.name, this.isSource);
							if (JSON.stringify(this.lastValid) != JSON.stringify(newEndpoint)) {

								this.lastValid = newEndpoint;
								this.endpointUpdated.emit(newEndpoint);
							}
						}
					}
				}
			)
		);

	}

	/**
	 * Called when whant to change the possible nodes.
	 */
	private updatePossibleNodes(value: EditorNode | string | null = null) {

		this.possibleNodes = [];
		var regexp = toRegexp(value);
		for (var node of this.topology.nodes) {

			if (regexp.test(node.name)) {

				this.possibleNodes.push(node);
			}
		}

		this.possibleNodes.sort((n1, n2) => n1.compareTo(n2));
	}



	/**
	 * Called when whant to change the possible channels.
	 */
	private updatePossibleChannels(value: ChannelSchema | string | null = null) {

		this.possibleChannels = [];
		var node = this.endpointForm.controls.node.value;
		if (node != null && typeof node === 'object') {

			if (node.component! != null && node.component.channels != null) {

				var pattern = toRegexp(value);
				for (var channel of node.component.channels) {

					if (channel.name != null && pattern.test(channel.name)
						&& ((channel.publish != null && this.isSource)
							|| (channel.subscribe != null && !this.isSource))
					) {

						this.possibleChannels.push(channel);
					}
				}

				this.possibleChannels.sort((c1, c2) => c1.name!.localeCompare(c2.name!));
			}
			if (this.endpointForm.controls.channel.disabled) {

				this.endpointForm.controls.channel.enable();
			}


		} else if (this.endpointForm.controls.channel.enabled) {

			this.endpointForm.controls.channel.disable();
		}
	}

	/**
	 * Register to the changes in the node.
	 */
	public ngOnDestroy() {

		for (var subscription of this.subscruptions) {

			subscription.unsubscribe();
		}

		this.subscruptions.splice(0, this.subscruptions.length);
	}



	/**
	 * Set the endpoint to edit.
	 */
	@Input()
	public set endpoint(endpoint: EditorEndpoint | null | undefined) {

		var channel: ChannelSchema | null = null;
		var node = this.topology.getNodeWith(endpoint?.nodeId);
		if (node != null && node.component != null && node.component.channels != null) {

			channel = node.component.channels.find(c => c.name == endpoint?.channel) || null;
		}
		this.lastValid = endpoint || null;
		this.endpointForm.setValue(
			{
				node: node,
				channel: channel
			},
			{
				emitEvent: false
			}
		);
		this.updatePossibleNodes(node);
		this.updatePossibleChannels(channel);
	}

	/**
	 * Return the text to display for a node. 
	 */
	public displayNode(node: EditorNode | string | null): string {

		if (node == null || typeof node === 'string') {

			return '';

		} else if (node.component != null && node.component.name != null) {

			return node.component.name;

		} else if (node.fisrtEndPointWithIsSource != null) {

			return 'NOTIFICATION TO DO';

		} else {

			return node.id;
		}

	}

	/**
	 * Return the text to display for a channel. 
	 */
	public displayChannel(channel: ChannelSchema | string | null): string {

		if (channel == null || typeof channel === 'string') {

			return '';

		} else {

			var name = toChannelName(channel.name);
			if (name == null) {
				name = '';
			}
			return name;
		}

	}



}
