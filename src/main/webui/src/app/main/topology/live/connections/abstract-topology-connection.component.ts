/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable, OnInit } from '@angular/core';

import { Observable, EMPTY } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { MessagesService } from '@shared/messages';
import { MovApiService, TopologyConnection } from '@shared/mov-api';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable()
export abstract class AbstractTopologyConnectionComponent implements OnInit {

	/**
	 * The indeitifer of the connection to show.
	 */
	public connectionId: string | null = null;

	/**
	 * The connection to show.
	 */
	public connection$: Observable<TopologyConnection> | null = null;


	/**
	 *  Create the connection.
	 */
	constructor(
		protected mov: MovApiService,
		protected route: ActivatedRoute,
		protected router: Router,
		protected messages: MessagesService
	) {

	}

	/**
	 * Initialize the connection.
	 */
	ngOnInit(): void {

		this.connection$ = this.route.paramMap.pipe(
			switchMap(
				(params) => {

					this.connectionId = params.get('id');
					if (this.connectionId != null) {

						return this.mov.getTopologyConnection(this.connectionId).pipe(
							catchError(err => {

								this.messages.showError($localize`:The error message when can get the connection@@main_topology_connections_abstract_code_get-error:Cannot get the topology connection information`);
								console.error(err);
								this.router.navigate(["/main/topology/live/connections"]);
								return EMPTY;
							})
						);

					} else {

						return EMPTY;
					}
				}
			)
		);
	}

}
