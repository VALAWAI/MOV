/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { MessagesService } from '@app/shared/messages';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { ComponentDefinitionPage, ComponentDefinition, ComponentType, MovApiService, ChannelSchema, sortChannelSchemaByName } from '@app/shared/mov-api';
import { MatSelectModule } from '@angular/material/select';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { toPattern } from '@app/shared';
import { EditorModule } from './editor.module';
import { TopologyEditorService } from './topology.service';
import { EditorNode } from './editor-node.model';
import { MatButtonModule } from '@angular/material/button';
import { ChangeNodeComponentAction, ChangeNodePositionAction, RemoveNodeEndpointAction } from './actions';
import { RemoveNodeAction } from './actions/remove-node.action';
import { GraphModule } from '@app/shared/graph';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { SelectChannelDialog } from './select-channel.dialog';
import { ChannelSchemaViewComponent } from './channel-view.component';


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
    MatAutocompleteModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatIconModule,
    EditorModule,
    MatButtonModule,
    GraphModule,
    MatDialogModule,
    ChannelSchemaViewComponent
],
	templateUrl: './node-form.component.html'
})
export class TopologyNodeFormComponent implements OnInit, OnDestroy {

	/**
	 * Notify when teh node has been updated.
	 */
	private readonly topology = inject(TopologyEditorService);

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
	 * The posible channels.
	 */
	public possibleChannels: ChannelSchema[] = [];

	/**
	 * The posible channels.
	 */
	public activeChannels: ChannelSchema[] = [];

	/**
	 * The component to manage the dialogs.
	 */
	private readonly dialog = inject(MatDialog);

	/**
	 * Service over the changes.
	 */
	private readonly ref = inject(ChangeDetectorRef);


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
				positionY: node.position.y,

			},
			{
				emitEvent: false
			}
		);


		this.possibleChannels = [];
		this.activeChannels = [];
		if (node.component != null && node.component.channels != null) {

			for (var channel of node.component.channels) {

				if (node.searchEndpoint(channel.name, channel.publish != null) == null) {

					this.possibleChannels.push(channel);

				} else {

					this.activeChannels.push(channel);
				}
			}
			sortChannelSchemaByName(this.possibleChannels);
			sortChannelSchemaByName(this.activeChannels);
		}

		if (node.sourceNotification == null) {

			this.nodeForm.controls.component.enable();

		} else {

			this.nodeForm.controls.component.disable();
		}

		this.updatePage();
	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(
			this.topology.changed$.subscribe(
				{
					next: action => {

						if (action.type == 'CHANGED_NODE' && this.nodeForm.controls.id.value == action.id) {

							this.node = this.topology.getNodeWith(action.id)!;
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
							const node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
							if (node.component == null || node.component.id != value.id) {

								var action = new ChangeNodeComponentAction(node.id, value);
								this.topology.apply(action);
							}
						}
					}
				}
			)
		);

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

					var action = new ChangeNodePositionAction(node.id, { x: x.value!, y: y.value! });
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

	/**
	 * Called when the user what to remove the editing node.
	 */
	public removeNode() {

		var action = new RemoveNodeAction(this.nodeForm.controls.id.value!);
		this.topology.apply(action);

	}

	/**
	 * The selected component.
	 */
	public get selectedComponent(): ComponentDefinition | null {

		var component = this.nodeForm.controls.component.value;
		if (typeof component === 'object') {

			return component as ComponentDefinition;
		} else {
			return null;
		}
	}

	/**
	 * Add an endpoint to be edited.
	 */
	public addEndpoint() {

		if (this.possibleChannels.length == 1) {
			//only one add it automatically
			const channel = this.possibleChannels.splice(0, 1)[0];
			this.activeChannels.push(channel);
			sortChannelSchemaByName(this.activeChannels);
			const node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
			node.searchEndpointOrCreate(channel.name, channel.publish != null);
			this.ref.markForCheck();
			this.ref.detectChanges();


		} else {

			this.dialog.open(SelectChannelDialog, { data: this.possibleChannels }).afterClosed().subscribe(
				{
					next: channel => {

						if (channel != null) {

							const index = this.possibleChannels.findIndex(c => c.name === channel.name);
							this.possibleChannels.splice(index, 1);
							this.activeChannels.push(channel);
							sortChannelSchemaByName(this.activeChannels);
							const node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
							node.searchEndpointOrCreate(channel.name, channel.publish != null);
							this.ref.markForCheck();
							this.ref.detectChanges();

						}
					}
				}
			);

		}

	}

	/**
	 * Called when the user what to remove the editing endpoint.
	 */
	public removeEndpoint(channel: ChannelSchema) {

		// update the form data
		this.possibleChannels.push(channel);
		sortChannelSchemaByName(this.possibleChannels);
		const index = this.activeChannels.findIndex(c => c.name === channel.name);
		this.activeChannels.splice(index, 1);
		this.ref.markForCheck();
		this.ref.detectChanges();


		// update the node
		const node = this.topology.getNodeWith(this.nodeForm.controls.id.value)!;
		const endpoint = node.searchEndpoint(channel.name, channel.publish != null)!;
		var action = new RemoveNodeEndpointAction(endpoint);
		this.topology.apply(action);

	}


}
