/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StatusNode } from './status-node.model';
import { MatIconModule } from '@angular/material/icon';
import { AbstractConnectionDetailComponent } from './abstract-connection-detail.component';

@Component({
	standalone: true,
	selector: 'app-notification-connection-detail',
	imports: [
		CommonModule,
		RouterModule,
		MatIconModule
	],
	templateUrl: './notification-connection-detail.component.html'
})
export class NotificationConnectionDetailComponent extends AbstractConnectionDetailComponent {

	/**
	 * The notification node.
	 */
	private notification: StatusNode | null = null;

	/**
	 * Obtain the data to show on the details.
	 */
	protected override updateData(): void {

		super.updateData();
		if (this._nodes.length > 0 && this._connection != null) {

			this.notification = this._nodes.find(node => node.id == this._connection!.target.nodeId)!;
		}

	}

	/**
	 * Return the notification identifier.
	 */
	public get notificationId(): string {

		return this.notification?.id || '';

	}

	/**
	 * Return the notification name.
	 */
	public get notificationName(): string {

		return this.notification?.name || '';

	}

	/**
	 * Return the notification channel.
	 */
	public get notificationChannel(): string {

		return this._connection?.target?.name || '';

	}

}
