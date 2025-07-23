/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DesignTopologyConnection, TopologyConnectionEndpoint, TopologyGraphConnectionType } from '@app/shared/mov-api';
import { TopologyConnectionEndpointEditorComponent } from './endpoint-editor.component';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Subscription } from 'rxjs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NotificationChangedEvent } from './notification-changed.event';
import { EditorTopology } from './editor-topology.model';



@Component({
	standalone: true,
	selector: 'app-topology-connection-editor',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		TopologyConnectionEndpointEditorComponent,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule,
		MatSlideToggleModule
	],
	templateUrl: './connection-editor.component.html'
})
export class TopologyConnectionEditorComponent implements OnInit, OnDestroy {

	/**
	 * The topology where the connection is defined.
	 */
	@Input()
	public topology: EditorTopology | null = null;

	/**
	 * Notify when the node has been updated.
	 */
	@Output()
	public connectionUpdated = new EventEmitter<DesignTopologyConnection>();

	/**
	 * Notify when the node has been updated.
	 */
	@Output()
	public notificationsChanged = new EventEmitter<NotificationChangedEvent>();

	/**
	 * The form to edit the connection.
	 */
	public connectionForm = new FormGroup(
		{
			source: new FormControl<TopologyConnectionEndpoint | null>(null),
			target: new FormControl<TopologyConnectionEndpoint | null>(null),
			convertCode: new FormControl<string | null>(null),
			type: new FormControl<TopologyGraphConnectionType | null>(null, Validators.required),
			notifications: new FormControl<boolean>(true, Validators.required),
			notificationX: new FormControl<number | null>(null),
			notificationY: new FormControl<number | null>(null),
		}
	);

	/**
	 * The subscription to the changes of the connection form.
	 */
	public connectionStatusSubscription: Subscription | null = null;

	/**
	 * The the last valid connection.
	 */
	private lastValid: DesignTopologyConnection | null = null;

	/**
	 * The subscription to the changes of the connection form.
	 */
	public changeNotificationsSubscription: Subscription | null = null;

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.connectionStatusSubscription = this.connectionForm.statusChanges.subscribe(
			{
				next: status => {

					if (status == 'VALID') {

						var newConnection = new DesignTopologyConnection();
						newConnection.source = this.connectionForm.controls.source.value || null;
						newConnection.target = this.connectionForm.controls.target.value || null;
						newConnection.convertCode = this.connectionForm.controls.convertCode.value || null;
						newConnection.type = this.connectionForm.controls.type.value || null;
						if (this.connectionForm.controls.notifications.value === true) {

							newConnection.notificationPosition = {
								x: this.connectionForm.controls.notificationX.value || 0,
								y: this.connectionForm.controls.notificationY.value || 0
							};
						}

						if (JSON.stringify(this.lastValid) != JSON.stringify(newConnection)) {

							this.lastValid = newConnection;
							this.connectionUpdated.emit(newConnection);
						}
					}
				}
			}
		);

		this.changeNotificationsSubscription = this.connectionForm.controls.notifications.valueChanges.subscribe(
			{
				next: value => {

					if (value) {

						var source = this.topology?.nodes.find(n => n.id == this.connectionForm.controls.source.value?.nodeTag);
						var target = this.topology?.nodes.find(n => n.id == this.connectionForm.controls.target.value?.nodeTag);
						var x = ((source?.position.x || 0) + (target?.position.x || 0)) / 2.0;
						this.connectionForm.controls.notificationX.setValue(x);
						var y = ((source?.position.y || 0) + (target?.position.y || 0)) / 2.0;
						this.connectionForm.controls.notificationY.setValue(y);
						//Notify Add notificaiton node

					} else {

						//Remove Notify Add notificaiton node

					}
				}
			}
		);

	}

	/**
	 * Called whne the component is destroyed.
	 */
	public ngOnDestroy(): void {

		if (this.connectionStatusSubscription != null) {

			this.connectionStatusSubscription.unsubscribe();
			this.connectionStatusSubscription = null;
		}
		if (this.changeNotificationsSubscription != null) {

			this.changeNotificationsSubscription.unsubscribe();
			this.changeNotificationsSubscription = null;
		}

	}

	/**
	 * Set the conneciton to edit.
	 */
	@Input()
	public set connection(connection: DesignTopologyConnection | null | undefined) {

		this.connectionForm.patchValue(
			{
				source: connection?.source || null,
				target: connection?.target || null,
				convertCode: connection?.convertCode || null,
				type: connection?.type || null,
				notifications: connection?.notificationPosition != null || (connection?.notifications != null && connection?.notifications.length > 0) || false,
				notificationX: connection?.notificationPosition?.x || null,
				notificationY: connection?.notificationPosition?.y || null

			},
			{
				emitEvent: false
			}
		);

	}

	/**
	 * Check if the connection can have notifications.
	 */
	public notificationAllowed(): boolean {

		return true;
	}


}
