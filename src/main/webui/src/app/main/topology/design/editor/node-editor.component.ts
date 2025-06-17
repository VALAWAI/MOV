/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { TopologyViewNodeModel } from './topolofy-view-node.model';
import { MessagesService } from '@app/shared/messages';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComponentDefinitionPage, ComponentDefinition, ComponentType, MovApiService } from '@app/shared/mov-api';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';



@Component({
	standalone: true,
	selector: 'app-topology-node-editor',
	imports: [
		CommonModule,
		MatAutocompleteModule,
		MatInputModule,
		MatFormFieldModule,
		ReactiveFormsModule,
		MatSelectModule
	],
	templateUrl: './node-editor.component.html'
})
export class TopologyNodeEditorComponent implements OnInit, OnDestroy {

	/**
	 * The ciew node that is editing.
	 */
	private viewNode: TopologyViewNodeModel | null = null;


	/**
	 * The form to edit the component.
	 */
	public componentForm = new FormGroup(
		{
			level: new FormControl<ComponentType | null>(null, Validators.required),
			component: new FormControl<ComponentDefinition | string | null>(null, {
				updateOn: 'change',
				validators: Validators.required
			})
		}
	);

	/**
	 * The subscription to the changes of the level form.
	 */
	public levelChangeSubscription: Subscription | null = null;

	/**
	 * The subscription to the changes of the component form.
	 */
	public componentChangeSubscription: Subscription | null = null;

	/**
	 * The page with the components that match the name.
	 */
	public page: ComponentDefinitionPage | null = null;

	/**
	 *  Create the component.
	 */
	constructor(
		private api: MovApiService,
		private messages: MessagesService
	) {

	}

	/**
	 * The node to edit.
	 */
	@Input()
	public set node(node: TopologyViewNodeModel | null) {

		this.viewNode = node;
		if (node != null) {


		} else {

			this.componentForm.setValue({
				level: null,
				component: null
			});
		}

	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.levelChangeSubscription = this.componentForm.controls.level.valueChanges.subscribe(
			{
				next: () => this.updatePage()
			}
		);
		this.componentChangeSubscription = this.componentForm.controls.component.valueChanges.subscribe(
			{
				next: value => {

					if (value == null || typeof value === 'string') {

						this.updatePage()
					}
				}
			}
		);
	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {

		if (this.levelChangeSubscription != null) {

			this.levelChangeSubscription.unsubscribe();
			this.levelChangeSubscription = null;
		}

		if (this.componentChangeSubscription != null) {

			this.componentChangeSubscription.unsubscribe();
			this.componentChangeSubscription = null;
		}

	}

	/**
	 * The function to display teh sleected component.
	 */
	public displayComponent(value: ComponentDefinition | string | null): string {

		if (typeof value === 'string') {

			return value;

		} else if (value?.name != null) {

			return value.name;
		}
		return '';
	}

	/**
	 * Called every time that has to update the page.
	 */
	private updatePage() {

		var pattern = "/.*";
		var component = this.componentForm.controls.component.value;
		if (typeof component == 'string') {

			if (component.length > 0) {

				pattern += component.replaceAll(/\W/g, '.*') + ".*";
			}

		} else if (component?.name != null) {

			pattern += component.name.replaceAll(/\W/g, '.*') + ".*";
		}
		pattern += "/i";
		var type = this.componentForm.controls.level.value;
		this.api.getComponentDefinitionPage(pattern, type, "name", 0, 10).subscribe(
			{
				next: page => this.page = page,
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}

}
