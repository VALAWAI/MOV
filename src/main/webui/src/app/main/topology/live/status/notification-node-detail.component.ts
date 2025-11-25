/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StatusNode } from './status-node.model';
import { MatIconModule } from '@angular/material/icon';
import { AbstractConnectionDetailComponent } from './abstract-connection-detail.component';
import { LiveTopologyComponentOutConnection } from '@app/shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-notification-node-detail',
	imports: [
    RouterModule,
    MatIconModule
],
	templateUrl: './notification-node-detail.component.html'
})
export class NotificationNodeDetailComponent extends AbstractConnectionDetailComponent {

	/**
	 * The node to show the detail.
	 */
	protected _node: StatusNode | null = null;

	/**
	 * Set the node to show the detail.
	 */
	@Input()
	public set node(node: StatusNode) {

		this._node = node;
		this.updateData();

	}

	/**
	 * Obtain the data to show on the details.
	 */
	protected override updateData() {

		super.updateData();
		if (this._nodes.length > 0 && this._node != null) {

			this.updateDataWith(this._node.model as LiveTopologyComponentOutConnection);
		}
	}


}
