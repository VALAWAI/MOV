/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject, model, OnDestroy, OnInit } from "@angular/core";
import { AbstractControl, FormControl, ReactiveFormsModule, ValidationErrors } from "@angular/forms";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { toPattern } from "@app/shared";
import { MessagesService } from "@app/shared/messages";
import { MovApiService, MinTopology, MinTopologyPage } from "@app/shared/mov-api";
import { Subscription } from "rxjs";

function requiredTopologyValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}

@Component({
	standalone: true,
	selector: 'dialog-select-topology-to-open',
	templateUrl: 'select-topology-to-open.dialog.html',
	imports: [
		MatButtonModule,
		MatFormFieldModule,
		MatInputModule,
		MatAutocompleteModule,
		ReactiveFormsModule,
		MatDialogModule
	],
})
export class SelectTopologyToOpenDialog implements OnInit, OnDestroy {

	/**
	 * The controller for the selected topology.
	 */
	public topologyForm = new FormControl<MinTopology | string | null>(null, requiredTopologyValidator);

	/**
	 * The subscription to the changes of the topology form.
	 */
	private topologyChangeSubscription: Subscription | null = null;

	/**
	 * Service to access to teh MOV API.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The service to show messages.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * The page of the found topologies
	 */
	public page: MinTopologyPage | null = null;

	/**
	 * Initialize the topology.
	 */
	public ngOnInit(): void {

		this.topologyChangeSubscription = this.topologyForm.valueChanges.subscribe(
			{
				next: value => {

					if (value != null && typeof value === 'string') {

						var pattern = toPattern(value);;
						this.api.getMinTopologyPage(pattern, "name", 0, 10).subscribe(
							{
								next: page => this.page = page,
								error: err => this.messages.showMOVConnectionError(err)
							}
						);

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

	/**
	 * The function to display teh sleected topology.
	 */
	public displayTopology(value: MinTopology | string | null): string {

		if (typeof value === 'string') {

			return value;

		} else if (value?.name != null) {

			return value.name;
		}
		return '';
	}

}