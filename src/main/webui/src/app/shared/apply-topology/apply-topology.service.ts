/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { inject, Injectable } from '@angular/core';
import { LiveConfiguration, MinTopology, MovApiService } from '../mov-api';
import { MessagesService } from '../messages';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmApplyTopologyDialog } from './confirm-apply-topology.dialog';

@Injectable({
	providedIn: 'root'
})
export class ApplyTopologyService {

	/**
	 * The service to interact with the MOV.
	 */
	private api = inject(MovApiService);

	/**
	 * The service to provide messages.
	 */
	private messages = inject(MessagesService);

	/**
	 * The service to manage the dialogs.
	 */
	private dialog = inject(MatDialog);


	/**
	 * Apply a topology to the MOV.
	 * 
	 * @param topology The topology to apply. 
	 */
	public confirmAndApplyTopology(topology: MinTopology, success?: (value: LiveConfiguration) => void) {

		this.dialog.open(ConfirmApplyTopologyDialog, { data: topology }).afterClosed().subscribe(
			{
				next: result => {

					if (result) {

						var conf = new LiveConfiguration();
						conf.topologyId = topology.id!;
						conf.createConnection = "APPLY_TOPOLOGY";
						conf.registerComponent = "APPLY_TOPOLOGY";
						this.api.setLiveConfiguration(conf).subscribe(
							{
								next: updated => {

									this.messages.showSuccess(
										$localize`:The success message when apply a topology@@sahred_apply_topology_success-msg:The MOV is now following the new topology.`
									);
									if (success != null) {

										success(updated);
									}
								},
								error: err => this.messages.showMOVConnectionError(err)
							}
						);

					}
				}
			}
		);


	}

}

