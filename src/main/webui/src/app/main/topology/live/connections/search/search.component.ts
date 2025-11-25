/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Observable, retry, Subscription, switchMap, timer } from 'rxjs';
import { MessageComponent } from 'src/app/shared/messages';
import { MatCheckbox } from '@angular/material/checkbox';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MinConnectionPage, MovApiService } from '@app/shared/mov-api';
import { MatTableModule } from '@angular/material/table';
import { MatMenuModule } from '@angular/material/menu';
import { ConfigService, toPattern } from '@app/shared';


@Component({
	standalone: true,
	selector: 'app-topology-connections-search',
	imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatInput,
    MatCheckbox,
    MatButton,
    RouterLink,
    MatMenuModule,
    MatIcon,
    MatPaginator,
    MatTableModule,
    MessageComponent
],
	templateUrl: './search.component.html'
})
export class TopologyConnectionsSearchComponent implements OnInit, OnDestroy {

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
	 * Subscription to the page changes.
	 */
	private pageSubscription: Subscription | null = null;

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
		private fb: FormBuilder,
		private route: ActivatedRoute,
		private conf: ConfigService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the connections component@@main_topology_connections_code_page-title:Topology connections`);
		this.startUpdatePage();
		this.formChanged = this.form.valueChanges.subscribe(
			{
				next: () => this.startUpdatePage()
			}
		);
		this.componentIdChanged = this.route.queryParamMap.subscribe({
			next: (params) => {

				this.componentId = params.get("componentId");
				this.startUpdatePage();
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
		if (this.pageSubscription != null) {

			this.pageSubscription.unsubscribe();
			this.pageSubscription = null;
		}

	}

	/**
	 * Called when the page is changed.
	 */
	public pageChanged(event: PageEvent) {

		this.pageIndex = event.pageIndex;
		this.pageSize = event.pageSize;
		this.startUpdatePage();
	}

	/**
	 * Called when has to trat to update the page.
	 */
	private startUpdatePage() {

		if (this.pageSubscription != null) {

			this.pageSubscription.unsubscribe();
		}

		this.pageSubscription = timer(0, this.conf.pollingTime).pipe(
			switchMap(() => this.getPage()),
			retry()
		).subscribe(
			{
				next: page => {

					if (!MinConnectionPage.equals(this.page, page)) {

						this.page = page
					}
				}
			}
		);
	}

	/**
	 * Called to update the page.
	 */
	private getPage(): Observable<MinConnectionPage> {

		var value = this.form.value;
		var pattern = toPattern(value.pattern);
		var orderBy = value.orderBy;
		if (value.reverse) {

			orderBy = "-" + orderBy;
		}
		var offset = this.pageIndex * this.pageSize;
		return this.mov.getMinConnectionPage(pattern, this.componentId, orderBy, offset, this.pageSize);

	}



}
