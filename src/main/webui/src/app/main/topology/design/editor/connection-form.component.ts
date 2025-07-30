/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DesignTopologyConnection } from '@app/shared/mov-api';
import { EndpointEditorComponent } from './endpoint-editor.component';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Subscription } from 'rxjs';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { EditorTopologyService } from './editor-topology.service';
import { EditorConnection } from './editor-connection.model';
import { EditorEndpoint } from './editor-endpoint.model';
import { ChangeConnectionTargetAction } from './actions';



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
		MatSlideToggleModule
	],
	templateUrl: './connection-form.component.html'
})
export class TopologyConnectionFormComponent implements OnInit, OnDestroy {

	/**
	 * The topology where the connection is defined.
	 */
	public readonly topology = inject(EditorTopologyService);

	/**
	 * Service over the changes.
	 */
	private readonly ref = inject(ChangeDetectorRef);


	/**
	 * The form to edit the connection.
	 */
	public connectionForm = new FormGroup(
		{
			id: new FormControl<string>({ value: '', disabled: true }),
			source: new FormControl<EditorEndpoint | null>(null),
			target: new FormControl<EditorEndpoint | null>(null),
			convertCode: new FormControl<string | null>(null),
			type: new FormControl<string | null>(null, Validators.required)
		}
	);

	/**
	 * The subscription to the changes of the connection form.
	 */
	public subscriptions: Subscription[] = [];

	/**
	 * The the last valid connection.
	 */
	private lastValid: DesignTopologyConnection | null = null;

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.subscriptions.push(
			this.topology.topologyChanged$.subscribe(
				{
					next: action => {

						if (action instanceof ChangeConnectionTargetAction && action.connectionId == this.connectionForm.controls.id.value) {

							if (JSON.stringify(this.connectionForm.controls.target.value) !== JSON.stringify(action.newTargetEndpoint)) {

								this.connectionForm.controls.target.setValue(action.newTargetEndpoint, { emitEvent: false });
								this.ref.markForCheck();
								this.ref.detectChanges();
							}
							/*
							var hasNotifications = (updatedConnection.sourceNotification != null);
							if (this.connectionForm.controls.notifications.value === hasNotifications) {

								this.connectionForm.controls.notifications.setValue(hasNotifications, { emitEvent: false });
							}
							*/
							// the other changes are doen by this editor
						}
					}
				}
			)
		);

		/*
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
							//this.connectionUpdated.emit(newConnection);
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
*/
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

		this.connectionForm.setValue(
			{
				id: connection.id,
				source: connection.source,
				target: connection.target,
				convertCode: connection.convertCode,
				type: connection.type
			},
			{
				emitEvent: false
			}
		);

		/*
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
		*/

	}

	/**
	 * Check if the connection can have notifications.
	 */
	public notificationAllowed(): boolean {

		return true;
	}

	/**
	 * Called whne the target of the connection must be changed.
	 */
	public targetEndpointChanged(newTarget: EditorEndpoint) {

		var connection = this.topology.getConnectionWith(this.connectionForm.controls.id.value!)!;
		if (JSON.stringify(connection.target) !== JSON.stringify(newTarget)) {

			var action = new ChangeConnectionTargetAction(this.connectionForm.controls.id.value!, newTarget);
			this.topology.apply(action);

		}
	}


}
