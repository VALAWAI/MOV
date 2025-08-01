/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EndpointEditorComponent } from './endpoint-editor.component';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Subscription } from 'rxjs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TopologyEditorService } from './topology.service';
import { EditorConnection } from './editor-connection.model';
import { EditorEndpoint } from './editor-endpoint.model';
import { ChangeConnectionConvertCodeAction, ChangeConnectionSourceAction, ChangeConnectionTargetAction, ChangeConnectionTypeAction, DisableConnectionNotificationsAction, EnableConnectionNotificationsAction, RemoveConnectionAction } from './actions';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { normalizeString } from '@app/shared';
import { EditorNode } from './editor-node.model';
import { GraphModule } from '@app/shared/graph';



@Component({
	standalone: true,
	selector: 'app-topology-connection-form',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		EndpointEditorComponent,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule,
		MatSlideToggleModule,
		MatButtonModule,
		MatIconModule,
		GraphModule
	],
	templateUrl: './connection-form.component.html'
})
export class TopologyConnectionFormComponent implements OnInit, OnDestroy {

	/**
	 * The topology where the connection is defined.
	 */
	public readonly topology = inject(TopologyEditorService);

	/**
	 * This is {@code true} if the connection cna have notifications.
	 */
	public notificationAllowed: boolean = true;

	/**
	 * This is {@code true} if the connection is a notification.
	 */
	public isNotification: boolean = false;

	/**
	 * The topology where the connection is defined.
	 */
	private readonly fb = inject(FormBuilder);

	/**
	 * The form to edit the connection.
	 */
	public connectionForm = this.fb.group(
		{
			id: this.fb.control<string>({ value: 'connection_0', disabled: true }),
			source: this.fb.control<EditorEndpoint | null>(null),
			target: this.fb.control<EditorEndpoint | null>(null),
			type: this.fb.control<string | null>(null, Validators.required),
			convertCode: this.fb.control<string | null>(null, { updateOn: 'blur' }),
			notifications: this.fb.control<boolean>(false)
		}
	);

	/**
	 * The subscription to the changes of the connection form.
	 */
	private subscriptions: Subscription[] = [];

	/** 
	 * The source node.
	 */
	public sourceNode: EditorNode | null = null;

	/** 
	 * The source node.
	 */
	public sourceEndpoint: EditorEndpoint | null = null;

	/** 
	 * The target node.
	 */
	public targetNode: EditorNode | null = null;

	/** 
	 * The target node.
	 */
	public targetEndpoint: EditorEndpoint | null = null;

	/**
	 * The identifier of the connection to modify the target. 
	 */
	private connectionIdForTarget: string = '';

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(
			this.topology.changed$.subscribe(
				{
					next: action => {

						if (action.type == 'CHANGED_CONNECTION' && this.connectionForm.controls.id.value == action.id) {

							this.connection = this.topology.getConnectionWith(action.id)!;
						}
					}
				}

			)
		);

		this.subscriptions.push(
			this.connectionForm.controls.type.valueChanges.subscribe(
				{
					next: value => {

						const newType = normalizeString(value);
						const id = this.connectionForm.controls.id.value!;
						const connection = this.topology.getConnectionWith(id)!;
						if (connection.type != newType) {

							const action = new ChangeConnectionTypeAction(id, newType, this.connectionIdForTarget);
							this.topology.apply(action);
						}
					}
				}
			)
		);

		this.subscriptions.push(
			this.connectionForm.controls.convertCode.valueChanges.subscribe(
				{
					next: value => {

						const newConvertCode = normalizeString(value);
						const id = this.connectionForm.controls.id.value!;
						const connection = this.topology.getConnectionWith(id)!;
						if (connection.convertCode != newConvertCode) {

							const action = new ChangeConnectionConvertCodeAction(id, newConvertCode);
							this.topology.apply(action);
						}
					}
				}
			)
		);

		this.subscriptions.push(
			this.connectionForm.controls.notifications.valueChanges.subscribe(
				{
					next: value => {

						const id = this.connectionForm.controls.id.value!;
						const connection = this.topology.getConnectionWith(id)!;
						connection.target.channel == null
						if (connection.isPartial && value == false) {
							// disable the notifications
							var disableAction = new DisableConnectionNotificationsAction(id);
							this.topology.apply(disableAction);

						} else if (!connection.isPartial && value == true) {
							// enable the notifications.
							var enableAction = new EnableConnectionNotificationsAction(id);
							this.topology.apply(enableAction);

						}// else no modified
					}
				}
			)
		);

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
	 * Set the conneciton to edit.
	 */
	@Input()
	public set connection(connection: EditorConnection) {

		this.connectionIdForTarget = connection.id;
		const value = {
			id: connection.id,
			source: connection.source,
			target: connection.target,
			convertCode: connection.convertCode || null,
			type: connection.type || 'BEZIER',
			notifications: connection.isPartial
		};
		this.isNotification = connection.isNotification;
		this.notificationAllowed = !connection.isNotification;
		this.sourceNode = null;
		this.sourceEndpoint = connection.source || null;
		this.targetNode = null;
		this.targetEndpoint = null;
		const notificationNodeId = connection.notificationNodeId;
		if (notificationNodeId != null) {

			const notificationNode = this.topology.getNodeWith(notificationNodeId)!;
			this.sourceNode = this.topology.getNodeWith(notificationNode.sourceNotification!.nodeId)!;
			this.sourceEndpoint = this.sourceNode.searchEndpoint(notificationNode.sourceNotification!.channel, true)!;

			const notificationSourceId = notificationNode.notificationSource!.id;
			const notificationToTarget = this.topology.connections.find(c => !c.isNotification && c.source.id == notificationSourceId)!;
			this.targetNode = this.topology.getNodeWith(notificationToTarget.target!.nodeId)!;;
			this.targetEndpoint = this.targetNode.searchEndpoint(notificationToTarget.target!.channel, false)!;;
			if (!connection.isNotification) {

				const notificationTargetId = notificationNode.notificationTarget!.id;
				const sourceToNotification = this.topology.connections.find(c => c.target.id == notificationTargetId)!;

				value.id = sourceToNotification.id;
				value.source = this.sourceEndpoint;
				value.convertCode = sourceToNotification.convertCode || null;
				value.type = sourceToNotification.type || 'BEZIER';
				value.target = this.targetEndpoint;
				this.connectionIdForTarget = notificationToTarget.id!;
			}
		}

		this.connectionForm.setValue(value, { emitEvent: false });

	}


	/**
	 * Called whne the target of the connection must be changed.
	 */
	public targetEndpointChanged(newTarget: EditorEndpoint) {

		var connection = this.topology.getConnectionWith(this.connectionIdForTarget)!;
		if (JSON.stringify(connection.target) !== JSON.stringify(newTarget)) {

			var action = new ChangeConnectionTargetAction(this.connectionIdForTarget, newTarget);
			this.topology.apply(action);

		}
	}


	/**
	 * Called whne the source of the connection must be changed.
	 */
	public sourceEndpointChanged(newSource: EditorEndpoint) {

		var connection = this.topology.getConnectionWith(this.connectionForm.controls.id.value!)!;
		if (JSON.stringify(connection.source) !== JSON.stringify(newSource)) {

			var action = new ChangeConnectionSourceAction(this.connectionForm.controls.id.value!, newSource);
			this.topology.apply(action);

		}
	}

	/**
	 * Called when the user what to remove the editing connection.
	 */
	public removeConnection() {

		var action = new RemoveConnectionAction(this.connectionForm.controls.id.value!);
		this.topology.apply(action);

	}

}
