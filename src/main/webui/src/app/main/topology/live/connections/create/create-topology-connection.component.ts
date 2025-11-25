/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { ConnectionToCreate, MovApiService, Component as MOVComponent, LogRecord, ChannelSchema } from 'src/app/shared/mov-api';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComponentSelectorComponent } from '@app/shared/component/selector';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { LoadingComponent } from '@app/shared/loading';
import { MatOptionModule } from '@angular/material/core';

import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
	standalone: true,
	selector: 'app-topology-connections-create',
	imports: [
    ReactiveFormsModule,
    ComponentSelectorComponent,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    LoadingComponent,
    MatButtonModule,
    MatCheckboxModule
],
	templateUrl: './create-topology-connection.component.html',
	styleUrls: ['./create-topology-connection.component.css']
})
export class CreateTopologyConnectionComponent implements OnInit {

	/**
	 * The form to define the connection to add.
	 */
	public form = this.fb.group(
		{
			sourceChannel: this.fb.control<ChannelSchema | null>(null, Validators.required),
			targetChannel: this.fb.control<ChannelSchema | null>(null, Validators.required),
			enabled: this.fb.control<boolean>(false, Validators.required)
		}
	);

	/**
	 * The source component.
	 */
	public sourceComponent: MOVComponent | null = null;

	/**
	 * The source channel names.
	 */
	public sourceChannels: ChannelSchema[] = [];

	/**
	 * The target component.
	 */
	public targetComponent: MOVComponent | null = null;

	/**
	 * The target channel names.
	 */
	public targetChannels: ChannelSchema[] = [];

	/**
	 * This is {@code true} when is adding the connection.
	 */
	public adding: boolean = false;

	/**
	 * The timestamp of the last log.
	 */
	private lastLogTimestamp: number = 0;

	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private fb: FormBuilder,
		private mov: MovApiService,
		private messages: MessagesService,
		private router: Router
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the create connection@@main_topology_connections_create_code_page-title:Create topology connection`);

	}

	/**
	 * Add the connection.
	 */
	public addConnection() {

		if (this.form.valid && this.sourceComponent != null && this.targetComponent != null) {

			this.adding = true;
			this.mov.getLogRecordPage(null, null, null, null, "-timestamp", 0, 1).subscribe(
				{
					next: page => {

						if (page && page.logs != null && page.logs.length > 0) {

							this.lastLogTimestamp = page.logs[0].timestamp || 0;

						} else {

							this.lastLogTimestamp = 0;
						}
						this.createTopologyConnection();

					},
					error: () => {

						this.lastLogTimestamp = 0;
						this.createTopologyConnection();
					}
				}
			);



		} else {

			this.form.markAllAsTouched();
		}

	}

	/**
	 *
	 */
	private createTopologyConnection() {

		var connection = new ConnectionToCreate();
		connection.sourceComponent = this.sourceComponent?.id || '';
		connection.sourceChannel = this.form.value.sourceChannel?.name || '';
		connection.targetComponent = this.targetComponent?.id || '';
		connection.targetChannel = this.form.value.targetChannel?.name || '';
		connection.enabled = this.form.value.enabled || false;
		this.mov.createTopologyConnection(connection).subscribe({

			next: () => {

				this.checkAddedConnection(connection);

			}, error: err => {

				this.messages.showError($localize`:Error message when cannot add a new connection@@main_topology_connections_create_code_add-error:Cannot add the new connection`);
				console.error(err);
			}
		});
	}

	/**
	 * Check if the connection has been added.
	 */
	private checkAddedConnection(connection: ConnectionToCreate, times: number = 100) {

		this.mov.getLogRecordPage(null, null, null, null, "-timestamp", 0, 100).subscribe(
			{
				next: page => {

					if (page.logs != null) {

						for (var log of page.logs) {

							if (log.timestamp != null && log.timestamp > this.lastLogTimestamp && this.match(connection, log)) {

								if (log.level == 'INFO') {

									this.mov.getMinConnectionPage("/" + connection.sourceChannel + "|" + connection.targetChannel + "/",
										"/" + connection.sourceComponent + "|" + connection.targetComponent + "/", "-createTimestamp", 0, 100).subscribe(
											{
												next: page => {

													if (page.connections != null && page.connections.length > 0) {

														this.adding = false;
														this.messages.showSuccess($localize`:Success message when added a new connection@@main_topology_connections_create_code_add-success:Added the new connection`);
														this.router.navigate(['/main/topology/live/connections', page.connections[0].id, 'show']);

													} else {

														this.showAddError();
													}

												}, error: err => this.showAddError(err)
											}
										);

								} else {

									this.showAddError();
								}
								return;
							}
						}
					}

					if (times > 0) {

						setTimeout(() => this.checkAddedConnection(connection, times - 1), 1000);

					} else {

						this.showAddError();
					}

				}, error: err => this.showAddError(err)
			}
		);
	}

	/**
	 * Check if a connection is refered in a log message.
	 */
	private match(connection: ConnectionToCreate, log: LogRecord): boolean {

		if (log.payload != null) {

			if (connection.sourceComponent != null) {

				var content = log.payload;
				var index = content.indexOf(connection.sourceComponent);
				if (index > 0 && connection.sourceChannel != null) {

					content = content.substring(index + 1);
					index = content.indexOf(connection.sourceChannel);
					if (index > 0 && connection.targetComponent != null) {

						content = content.substring(index + 1);
						index = content.indexOf(connection.targetComponent);
						if (index > 0 && connection.targetChannel != null) {

							content = content.substring(index + 1);
							index = content.indexOf(connection.targetChannel);
							if (index > 0) {

								return true;
							}
						}
					}
				}

			}
		}

		return false;
	}

	/**
	 * Show error that cannot add the connection.
	 */
	private showAddError(err: any = null) {

		this.adding = false;
		this.messages.showError($localize`:Error message when cannot add a new connection@@main_topology_connections_create_code_add-error:Cannot add the new connection`);
		if (err != null) {
			console.error(err);
		}

	}

	/**
	 * Called when is selected a source.
	 */
	public selectedSource(component: MOVComponent | null) {

		if (this.sourceComponent == null || component == null || this.sourceComponent.id != component.id) {

			this.sourceComponent = component;
			this.sourceChannels.splice(0, this.sourceChannels.length);
			var selectedSchema: ChannelSchema | null = null;
			if (this.sourceComponent != null && this.sourceComponent.channels != null) {

				for (var channel of this.sourceComponent.channels) {

					if (channel.publish != null) {

						this.sourceChannels.push(channel);
					}
				}

				if (this.sourceChannels.length > 0) {

					this.sort(this.sourceChannels);
					selectedSchema = this.sourceChannels[0];
				}
			}
			setTimeout(() => this.form.controls.sourceChannel.setValue(selectedSchema), 5);
		}
	}

	/**
	 * Called when is selected a target.
	 */
	public selectedTarget(component: MOVComponent | null) {

		if (this.targetComponent == null || component == null || this.targetComponent.id != component.id) {

			this.targetComponent = component;
			this.targetChannels.splice(0, this.targetChannels.length);
			var selectedSchema: ChannelSchema | null = null;
			if (this.targetComponent != null && this.targetComponent.channels != null) {

				for (var channel of this.targetComponent.channels) {

					if (channel.subscribe != null) {

						this.targetChannels.push(channel);
					}
				}

				if (this.targetChannels.length > 0) {

					this.sort(this.targetChannels);
					selectedSchema = this.targetChannels[0];
				}
			}
			setTimeout(() => this.form.controls.targetChannel.setValue(selectedSchema), 5);
		}

	}

	/**
	 *
	 */
	private sort(channels: ChannelSchema[]) {

		channels.sort((c1, c2) => {

			if (c1.name == null) {

				if (c2.name == null) {

					return 0;

				} else {

					return 1;
				}

			} else if (c2.name == null) {

				return -1;

			} else {

				return c1.name.localeCompare(c2.name);
			}

		});

	}

}
