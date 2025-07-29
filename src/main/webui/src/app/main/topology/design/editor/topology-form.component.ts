/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MessagesService } from '@app/shared/messages';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ComponentDefinitionPage, ComponentDefinition, ComponentType, MovApiService, MinTopology, Point } from '@app/shared/mov-api';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { EditorTopologyService } from './editor-topology.service';
import { normalizeString } from '@app/shared';
import { ChangeTopologyAction, ChangeTopologyDescription, ChangeTopologyName } from './actions';


function requiredComponentValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}

@Component({
	standalone: true,
	selector: 'app-topology-form',
	imports: [
		CommonModule,
		MatAutocompleteModule,
		MatInputModule,
		MatFormFieldModule,
		ReactiveFormsModule,
		MatSelectModule,
		MatIconModule
	],
	templateUrl: './topology-form.component.html'
})
export class TopologyFormComponent implements OnInit, OnDestroy {

	/**
	 * The topology to edit.
	 */
	private readonly topology = inject(EditorTopologyService);

	/**
	 * The form to edit the component.
	 */
	public topologyForm = new FormGroup(
		{
			name: new FormControl<string | null>(this.topology.min.name, {
				updateOn: 'blur',
				validators: [
					Validators.required]
			}),
			description: new FormControl<string | null>(this.topology.min.description, {
				updateOn: 'blur'
			})
		}
	);


	/**
	 * The subscription to the changes of the topology form.
	 */
	private subscriptions: Subscription[] = [];


	/**
	 * Initialize the form.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(
			this.topology.topologyChanged$.subscribe(
				{
					next: action => {

						if (action instanceof ChangeTopologyAction) {

							this.topologyForm.setValue(
								{
									name: this.topology.min.name,
									description: this.topology.min.description
								},
								{
									emitEvent: false
								}
							);
						}
					}
				}
			)
		);
		this.subscriptions.push(
			this.topologyForm.controls.name.valueChanges.subscribe(
				{
					next: value => {

						var normalized = normalizeString(value);
						if (normalized != this.topology.min.name) {

							var action = new ChangeTopologyName(normalized);
							this.topology.apply(action);

						}
					}
				}
			)
		);
		this.subscriptions.push(
			this.topologyForm.controls.description.valueChanges.subscribe(
				{
					next: value => {

						var normalized = normalizeString(value);
						if (normalized != this.topology.min.description) {

							var action = new ChangeTopologyDescription(normalized);
							this.topology.apply(action);

						}
					}
				}
			)
		);
	}

	/**
	 * Called whne the form is destroyed.
	 */
	public ngOnDestroy(): void {


		for (var subscription of this.subscriptions) {

			subscription.unsubscribe();

		}

		this.subscriptions.splice(0, this.subscriptions.length);

	}



}
