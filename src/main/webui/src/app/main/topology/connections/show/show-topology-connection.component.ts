/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy } from '@angular/core';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { MovApiService } from 'src/app/shared/mov-api';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AbstractTopologyConnectionComponent } from '../abstract-topology-connection.component';
import { Subscription } from 'rxjs';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { LoadingComponent } from '@app/shared/loading';
import { TimestampPipe } from '@app/shared/timestamp';


@Component({
	standalone: true,
	selector: 'app-topology-connections-show',
	imports: [
		NgIf,
		MatIcon,
		RouterLink,
		MatButton,
		NgxJsonViewerModule,
		NgFor,
		LoadingComponent,
		TimestampPipe,
		AsyncPipe
	],
	templateUrl: './show-topology-connection.component.html',
	styleUrls: ['./show-topology-connection.component.css']
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
