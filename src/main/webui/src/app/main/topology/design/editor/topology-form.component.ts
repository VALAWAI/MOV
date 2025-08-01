/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { TopologyEditorService } from './topology.service';
import { normalizeString } from '@app/shared';
import { ChangeTopologyDescription, ChangeTopologyName } from './actions';


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
	private readonly topology = inject(TopologyEditorService);

	/**
	 * The form nbuilder.
	 */
	private readonly fb = inject(FormBuilder);

	/**
	 * The form to edit the component.
	 */
	public topologyForm = this.fb.group(
		{
			name: this.fb.control<string | null>(this.topology.min.name || null, {
				updateOn: 'blur',
				validators: [
					Validators.required]
			}),
			description: this.fb.control<string | null>(this.topology.min.description || null, {
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
			this.topology.changed$.subscribe(
				{
					next: action => {

						if (action.type == 'CHANGED_TOPOLOGY') {

							this.topologyForm.setValue(
								{
									name: this.topology.min.name||null,
									description: this.topology.min.description||null
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
