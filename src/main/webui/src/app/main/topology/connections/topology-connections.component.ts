/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { Subscription } from 'rxjs';
import { MessagesService } from 'src/app/shared/messages';
import { MinConnectionPage, MovApiService } from 'src/app/shared/mov-api';
import { ActivatedRoute } from '@angular/router';


@Component({
	selector: 'app-topology-connections',
	templateUrl: './topology-connections.component.html',
	styleUrls: ['./topology-connections.component.css']
})
export class TopologyConnectionsComponent implements OnInit, OnDestroy {

	/**
	 * The columns to display.
	 */
	public displayedColumns: string[] = ['source', 'target', 'enabled', 'actions'];

	/**
	 * The component to manage the messages.
	 */
	public form: FormGroup = this.fb.group(
		{
			pattern: this.fb.control<string | null>(null),
			orderBy: this.fb.control<string>("source"),
			reverse: this.fb.control<boolean>(false)
		});

	/**
	 * The page to show.
	 */
	public page: MinConnectionPage | null = null;

	/**
	 * The size for the pages.
	 */
	public pageSize: number = 15;

	/**
	 * The size for the pages.
	 */
	public pageIndex: number = 0;

	/**
	 * The subscription to the message changes.
	 */
	private formChanged: Subscription | null = null;

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
		private mov: MovApiService,
		private messages: MessagesService,
		private fb: FormBuilder,
		private route: ActivatedRoute
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the connections component@@main_topology_connections_code_page-title:Topology connections`);
		this.formChanged = this.form.valueChanges.subscribe(
			{
				next: () => this.updatePage()
			}
		);
		this.componentIdChanged = this.route.queryParamMap.subscribe({
			next: (params) => {

				this.componentId = params.get("componentId");
				this.updatePage();
			}
		});

	}

	/**
	 * Called whne the component is destroyed.
	 */
	ngOnDestroy(): void {

		if (this.formChanged != null) {

			this.formChanged.unsubscribe();
			this.formChanged = null;
		}
		if (this.componentIdChanged != null) {

			this.componentIdChanged.unsubscribe();
			this.componentIdChanged = null;
		}

	}

	/**
	 * Called when teh page is changed.
	 */
	public pageChanged(event: PageEvent) {

		this.pageIndex = event.pageIndex;
		this.pageSize = event.pageSize;
		this.updatePage();
	}

	/**
	 * Called to update the page.
	 */
	public updatePage() {

		var value = this.form.value;
		var pattern = value.pattern;
		if (pattern != null) {

			pattern = pattern.trim();
			if (pattern.length == 0) {

				pattern = null;

			} else {

				pattern = pattern.replace(/\*/, ".*");
				pattern = "/.*" + pattern + ".*/i";
			}

		}
		var orderBy = value.orderBy;
		if (value.reverse) {

			orderBy = "-" + orderBy;
		}
		var offset = this.pageIndex * this.pageSize;
		this.mov.getMinConnectionPage(pattern, this.componentId, orderBy, offset, this.pageSize).subscribe(
			{
				next: page => this.page = page,
				error: err => {

					this.messages.showError($localize`:The error message when can not get the topology connections@@main_topology_connections_code_get-error:Cannot obtain the topology connections`);
					console.error(err);
				}
			}
		);


	}



}
