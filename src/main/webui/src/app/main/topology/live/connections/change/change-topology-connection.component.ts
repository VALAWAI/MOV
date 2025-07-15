/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { ChangeConnection, MovApiService, TOPOLOGY_ACTION_NAMES, TopologyAction } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AbstractTopologyConnectionComponent } from '../abstract-topology-connection.component';
import { Subscription } from 'rxjs';
import { AsyncPipe, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { LoadingComponent } from '@app/shared/loading';
import { MatButton } from '@angular/material/button';

@Component({
	standalone: true,
	selector: 'app-topology-connections-change',
	imports: [
		NgIf,
		LoadingComponent,
		NgSwitch,
		NgSwitchCase,
		NgSwitchDefault,
		MatButton,
		RouterLink,
		AsyncPipe
	],
	templateUrl: './change-topology-connection.component.html',
	styleUrls: ['./change-topology-connection.component.css']
})
export class ChangeTopologyConnectionComponent extends AbstractTopologyConnectionComponent implements OnDestroy {

	/**
	 * This is {@code true} if it is changing.
	 */
	public changing: boolean = false;

	/**
	 * The action to do.
	 */
	public action: TopologyAction | null = null;

	/**
	 * Subscription for the action to do.
	 */
	private actionChanged: Subscription | null = null;

	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		protected override mov: MovApiService,
		protected override route: ActivatedRoute,
		protected override router: Router,
		protected override messages: MessagesService
	) {

		super(mov, route, router, messages);
	}

	/**
	 * Initialize the component.
	 */
	override ngOnInit(): void {

		super.ngOnInit();
		this.header.changeHeaderTitle($localize`:The header title for the unregister component@@main_topology_connections_change_code_page-title:Change topology connection`);

		this.actionChanged = this.route.queryParamMap.subscribe({
			next: (params) => {

				var action: string | null = params.get("action");
				if (action != null) {

					action = action.toLowerCase();
					for (var name of TOPOLOGY_ACTION_NAMES) {

						if (name.toLowerCase() == action) {

							this.action = name;
							break;
						}
					}
				}
			}
		});
	}

	/**
	 * Unsubscribe the component.
	 */
	ngOnDestroy(): void {

		if (this.actionChanged != null) {

			this.actionChanged.unsubscribe();
			this.actionChanged = null;
		}
	}

	/**
	 * Do the aciton over the connection.
	 */
	public doAction() {

		this.changing = true;
		var model = new ChangeConnection();
		model.action = this.action;
		model.connectionId = this.connectionId;
		this.mov.updateTopologyConnection(model).subscribe(
			{
				next: () => {

					this.checkActionDone();

				}, error: err => {

					this.changing = false;
					this.messages.showError($localize`:The error message when cannot update the topology connection@@main_topology_connections_change_error-message:Cannot update the topology connection`);
					console.error(err);
				}
			}
		);

	}

	/**
	 * Check if the action has been done.
	 */
	private checkActionDone(iter: number = 60) {

		if (this.connectionId != null) {

			this.mov.getTopologyConnection(this.connectionId).subscribe(
				{
					next: updated => {

						if (this.action == 'DISABLE' && updated.enabled == false) {

							this.changing = false;
							this.messages.showSuccess($localize`:The success message when the connection is disabled@@mmain_topology_connections_change_code_success-disable-msg:Disabled the topology connection`);
							this.router.navigate(["/main/topology/live/connections"]);

						} else if (this.action == 'ENABLE' && updated.enabled) {

							this.changing = false;
							this.messages.showSuccess($localize`:The success message when the connection is enabled@@mmain_topology_connections_change_code_success-enable-msg:Enabled the topology connection`);
							this.router.navigate(["/main/topology/live/connections"]);

						} else if (iter == 0) {

							this.changing = false;
							this.messages.showError($localize`:The error message when cannot update the topology connection@@main_topology_connections_change_error-message:Cannot update the topology connection`);

						} else {

							setTimeout(() => this.checkActionDone(iter - 1), 1000);
						}

					},
					error: () => {

						if (this.action == 'REMOVE') {

							this.messages.showSuccess($localize`:The success message when the connection is removed@@mmain_topology_connections_change_code_success-removed-msg:Removed the topology connection`);
							this.router.navigate(["/main/topology/live/connections"]);

						} else {

							this.messages.showError($localize`:The error message when cannot update the topology connection@@main_topology_connections_change_error-message:Cannot update the topology connection`);

						}
					}
				}
			);
		}

	}

}
