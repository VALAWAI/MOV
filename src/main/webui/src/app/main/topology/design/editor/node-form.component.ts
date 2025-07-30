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
import { EditorTopologyService } from './editor-topology.service';
import { EditorNode } from './editor-node.model';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { ChangeNodeComponentAction, ChangeNodePositionAction } from './actions';


function requiredComponentValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value == null || typeof control.value === 'string') {

		return { 'required': true };
	}
	return null;
}

@Component({
	standalone: true,
	selector: 'app-topology-node-form',
	imports: [
		CommonModule,
		MatAutocompleteModule,
		MatInputModule,
		MatFormFieldModule,
		ReactiveFormsModule,
		MatSelectModule,
		MatIconModule,
		EditorModule,
		MatButtonModule,
		MatMenuModule
	],
	templateUrl: './node-form.component.html'
})
export class TopologyNodeFormComponent implements OnInit, OnDestroy {

	/**
	 * Notify when teh node has been updated.
	 */
	private readonly topology = inject(EditorTopologyService);

	/**
	 * The form to edit the component.
	 */
	public nodeForm = new FormGroup(
		{
			id: new FormControl<string>({ value: 'node_0', disabled: true }),
			level: new FormControl<ComponentType | null>(null, Validators.required),
			component: new FormControl<ComponentDefinition | string | null>(null, {
				updateOn: 'change',
				validators: [requiredComponentValidator]
			}),
			positionX: new FormControl<number>(0.0, Validators.required),
			positionY: new FormControl<number>(0.0, Validators.required),
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
	public set node(node: EditorNode) {

		this.nodeForm.setValue(
			{
				id: node.id,
				level: node.component?.type || null,
				component: node.component,
				positionX: node.position.x,
				positionY: node.position.y

			},
			{
				emitEvent: false
			}
		);
	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(
			this.topology.topologyChanged$.subscribe(
				{
					next: action => {

						if (action instanceof ChangeNodePositionAction
							&& action.nodeId == this.nodeForm.controls.id.value
							&& (
								this.nodeForm.controls.positionX.value != action.newPosition.x
								|| this.nodeForm.controls.positionY.value != action.newPosition.y
							)
						) {

							this.nodeForm.patchValue(
								{
									positionX: action.newPosition.x,
									positionY: action.newPosition.y
								},
								{ emitEvent: false }
							);
						}
					}
				}

			)
		);

		this.subscriptions.push(this.nodeForm.controls.positionX.valueChanges.subscribe({ next: () => this.applyChangesInNodePosition() }));
		this.subscriptions.push(this.nodeForm.controls.positionY.valueChanges.subscribe({ next: () => this.applyChangesInNodePosition() }));


		this.subscriptions.push(
			this.nodeForm.controls.level.valueChanges.subscribe(
				{
					next: value => {

						var component = this.nodeForm.controls.component.value;
						if (component != null && typeof component !== 'string' && component.type != value) {

							this.nodeForm.controls.component.setValue(null, { emitEvent: false });
						}
						this.updatePage();

					}
				}
			)
		);
		this.subscriptions.push(
			this.nodeForm.controls.component.valueChanges.subscribe(
				{
					next: value => {

						if (value == null || typeof value === 'string') {

							this.updatePage()

						} else {
							// selected component
							var node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
							if (node.component == null || node.component.id != value.id) {

								var action = new ChangeNodeComponentAction(node.id, value);
								this.topology.apply(action);
							}
						}
					}
				}
			)
		);
		/*
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
		*/

	}

	/**
	 * Called when has to change the position of the node.
	 */
	private applyChangesInNodePosition() {

		var x = this.nodeForm.controls.positionX;
		if (x.valid) {

			var y = this.nodeForm.controls.positionY;
			if (y.valid) {

				var node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
				if (node.position.x != x.value || node.position.y != y.value) {

					var action = new ChangeNodePositionAction(node, { x: x.value!, y: y.value! });
					this.topology.apply(action);
				}
			}
		}

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
