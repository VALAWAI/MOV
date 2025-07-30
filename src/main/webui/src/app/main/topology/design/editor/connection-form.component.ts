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
import { ChangeConnectionSourceAction, ChangeConnectionTargetAction, RemoveConnectionAction } from './actions';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';



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
		MatIconModule
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
			id: new FormControl<string>({ value: 'connection_0', disabled: true }),
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

						} else if (action instanceof ChangeConnectionSourceAction && action.connectionId == this.connectionForm.controls.id.value) {

							if (JSON.stringify(this.connectionForm.controls.source.value) !== JSON.stringify(action.newSourceEndpoint)) {

								this.connectionForm.controls.source.setValue(action.newSourceEndpoint, { emitEvent: false });
								this.ref.markForCheck();
								this.ref.detectChanges();
							}
						}
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

		this.connectionForm.setValue(
			{
				id: connection.id,
				source: connection.source,
				target: connection.target,
				convertCode: connection.convertCode,
				type: connection.type || 'BEZIER'
			},
			{
				emitEvent: false
			}
		);

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
