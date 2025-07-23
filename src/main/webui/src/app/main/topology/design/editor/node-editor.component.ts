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
import { ComponentDefinitionPage, ComponentDefinition, ComponentType, MovApiService, TopologyNode, Point } from '@app/shared/mov-api';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { toPattern } from '@app/shared';
import { EditorModule } from './editor.module';
import { EditorService } from './editor.service';


function requiredComponentValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}

@Component({
	standalone: true,
	selector: 'app-topology-node-editor',
	imports: [
		CommonModule,
		MatAutocompleteModule,
		MatInputModule,
		MatFormFieldModule,
		ReactiveFormsModule,
		MatSelectModule,
		MatIconModule,
		EditorModule
	],
	templateUrl: './node-editor.component.html'
})
export class TopologyNodeEditorComponent implements OnInit, OnDestroy {

	/**
	 * The the last valid node.
	 */
	private lastValid: TopologyNode | null = null;

	/**
	 * Notify when teh node has been updated.
	 */
	@Output()
	public nodeUpdated = new EventEmitter<TopologyNode>();

	/**
	 * The form to edit the component.
	 */
	public nodeForm = new FormGroup(
		{
			tag: new FormControl<string>('node_0'),
			level: new FormControl<ComponentType | null>(null, Validators.required),
			component: new FormControl<ComponentDefinition | string | null>(null, {
				updateOn: 'change',
				validators: [requiredComponentValidator]
			}),
			positionX: new FormControl<number>(0.0, Validators.required),
			positionY: new FormControl<number>(0.0, Validators.required)
		}
	);

	/**
	 * The subscription to the changes of the node form.
	 */
	private subscriptions: Subscription[] = [];

	/**
	 * The page with the components that match the name.
	 */
	public page: ComponentDefinitionPage | null = null;

	/**
	 * The service of teh editor.
	 */
	private readonly editor = inject(EditorService);

	/**
	 * The service to interact withe MOV.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The service to show messages to the user.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * The node to edit.
	 */
	@Input()
	public set node(node: TopologyNode | null | undefined) {

		var value = {
			tag: 'node_0',
			level: null as ComponentType | null,
			component: null as ComponentDefinition | string | null,
			positionX: 0.0,
			positionY: 0.0
		};
		if (node != null) {

			value.tag = node.tag;
			value.level = node.component?.type || null;
			value.component = node.component;
			value.positionX = node.position.x;
			value.positionY = node.position.y;
		}

		this.nodeForm.patchValue(value, { emitEvent: false });

	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(this.nodeForm.statusChanges.subscribe(
			{
				next: status => {

					if (status == 'VALID') {

						var newNode = new TopologyNode();
						newNode.tag = this.nodeForm.controls.tag.value || 'node_0';
						newNode.component = this.nodeForm.controls.component.value as ComponentDefinition;
						newNode.position = new Point();
						newNode.position.x = this.nodeForm.controls.positionX.value || 0;
						newNode.position.y = this.nodeForm.controls.positionY.value || 0;

						if (JSON.stringify(this.lastValid) != JSON.stringify(newNode)) {

							this.lastValid = newNode;
							this.nodeUpdated.emit(newNode);
						}
					}
				}
			}
		));

		this.subscriptions.push(this.nodeForm.controls.level.valueChanges.subscribe(
			{
				next: value => {

					var component = this.nodeForm.controls.component.value;
					if (component != null && typeof component !== 'string' && component.type != value) {

						this.nodeForm.controls.component.setValue(null, { emitEvent: false });
					}
					this.updatePage();

				}
			}
		));
		this.subscriptions.push(this.nodeForm.controls.component.valueChanges.subscribe(
			{
				next: value => {

					if (value == null || typeof value === 'string') {

						this.updatePage()
					}
				}
			}
		));
		this.subscriptions.push(this.editor.movedNode$.subscribe(
			{
				next: event => {
					if (event.nodeId == this.nodeForm.controls.tag.value) {

						this.nodeForm.controls.positionX.setValue(event.point.x, { emitEvent: false });
						this.nodeForm.controls.positionY.setValue(event.point.y, { emitEvent: false });
					}
				}
			}

		));

	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {

		for (var subscription of this.subscriptions) {

			subscription.unsubscribe();

		}
		this.subscriptions = [];

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

		var pattern: string | null = null;
		var component = this.nodeForm.controls.component.value;
		if (typeof component == 'string') {

			pattern = toPattern(component);

		} else if (component?.name != null) {

			pattern = toPattern(component.name);
		}
		var type = this.nodeForm.controls.level.value;
		this.api.getComponentDefinitionPage(pattern, type, "name", 0, 10).subscribe(
			{
				next: page => this.page = page,
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}
	
	



}
