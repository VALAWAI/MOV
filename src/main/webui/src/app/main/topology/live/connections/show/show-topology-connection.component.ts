/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy } from '@angular/core';
import { MainService } from '@app/main';
import { MessagesService } from '@shared/messages';
import { MovApiService } from '@shared/mov-api';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AbstractTopologyConnectionComponent } from '../abstract-topology-connection.component';
import { Subscription } from 'rxjs';
import { AsyncPipe, CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { LoadingComponent } from '@app/shared/loading';
import { TimestampPipe } from '@app/shared/timestamp';
import { ChannelsViewComponent } from '@app/shared/channels-view';


@Component({
	standalone: true,
	selector: 'app-topology-connections-show',
	imports: [
		CommonModule,
		MatIcon,
		RouterLink,
		MatButton,
		LoadingComponent,
		TimestampPipe,
		AsyncPipe,
		ChannelsViewComponent
	],
	templateUrl: './show-topology-connection.component.html'
})
export class ShowTopologyConnectionComponent extends AbstractTopologyConnectionComponent implements OnDestroy {

	/**
	 * The component that show its connection.
	 */
	public componentId: string | null = null;

	/**
	 * Subscription for the component identifier changes.
	 */
	private componentIdChanged: Subscription | null = null;

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
		this.header.changeHeaderTitle($localize`:The header title for the show topology connection@@main_topology_connections_show_code_page-title:Show topology conection`);

		this.componentIdChanged = this.route.queryParamMap.subscribe({
			next: (params) => {

				this.componentId = params.get("componentId");
			}
		});
	}

	/**
	 * Unsubscribe the component.
	 */
	ngOnDestroy(): void {

		if (this.componentIdChanged != null) {

			this.componentIdChanged.unsubscribe();
			this.componentIdChanged = null;
		}
	}

}
