/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MessagesService } from '@app/shared/messages';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ComponentDefinitionPage, ComponentDefinition, ComponentType, MovApiService, MinTopology, Point } from '@app/shared/mov-api';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';


function requiredComponentValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}

@Component({
	standalone: true,
	selector: 'app-min-topology-editor',
	imports: [
		CommonModule,
		MatAutocompleteModule,
		MatInputModule,
		MatFormFieldModule,
		ReactiveFormsModule,
		MatSelectModule,
		MatIconModule
	],
	templateUrl: './min-topology-editor.component.html'
})
export class MinTopologyEditorComponent implements OnInit, OnDestroy {

	/**
	 * The topology that is editing.
	 */
	private editingTopology: MinTopology | null = null;

	/**
	 * Notify when teh node has been updated.
	 */
	@Output()
	public topologyUpdated = new EventEmitter<MinTopology>();

	/**
	 * The form to edit the component.
	 */
	public topologyForm = new FormGroup(
		{
			name: new FormControl<string | null>(null, Validators.required),
			description: new FormControl<string | null>(null)
		}
	);


	/**
	 * The subscription to the changes of the topology form.
	 */
	public topologyChangeSubscription: Subscription | null = null;


	/**
	 * The topology to edit.
	 */
	@Input()
	public set topology(topology: MinTopology | null) {

		if (JSON.stringify(this.editingTopology) != JSON.stringify(topology)) {

			this.editingTopology = topology;
			if (topology != null) {

				this.topologyForm.patchValue(
					{
						name: topology.name,
						description: topology.description
					},
					{ emitEvent: false }
				);

			} else {

				this.topologyForm.patchValue(
					{
						name: null,
						description: null
					},
					{ emitEvent: false }
				);
			}
		}

	}

	/**
	 * Initialize the topology.
	 */
	public ngOnInit(): void {


		this.topologyChangeSubscription = this.topologyForm.valueChanges.subscribe(
			{
				next: value => {

					var topology = new MinTopology();
					topology.id = this.editingTopology?.id || null;
					topology.name = this.topologyForm.controls.name.value;
					topology.description = this.topologyForm.controls.description.value;
					if (JSON.stringify(this.editingTopology) != JSON.stringify(topology)) {

						this.editingTopology = topology;
						this.topologyUpdated.emit(topology);
					}
				}
			}
		);
	}

	/**
	 * Called whne the topology is destroyed.
	 */
	public ngOnDestroy(): void {


		if (this.topologyChangeSubscription != null) {

			this.topologyChangeSubscription.unsubscribe();
			this.topologyChangeSubscription = null;
		}

	}



}
