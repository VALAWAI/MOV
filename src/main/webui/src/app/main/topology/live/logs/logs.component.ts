/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Observable, retry, Subscription, switchMap, timer } from 'rxjs';
import { MainService } from '@app/main';
import { COMPONENT_TYPE_NAMES, LOG_LEVEL_NAMES, LogRecord, LogRecordPage, MovApiService } from '@shared/mov-api';
import { ShowLogDialog } from './show-log.dialog';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { TimestampPipe } from '@app/shared/timestamp';
import { MatIcon } from '@angular/material/icon';
import { ComponentNameBeautifier } from '@app/shared/component/view';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { ConfigService, toPattern } from '@app/shared';


@Component({
	standalone: true,
	selector: 'app-logs',
	imports: [
		ReactiveFormsModule,
		MatFormField,
		MatInput,
		MatLabel,
		MatSelect,
		MatOption,
		TimestampPipe,
		MatIcon,
		ComponentNameBeautifier,
		MatPaginator,
		MatCheckbox,
		MatTableModule
	],
	templateUrl: './logs.component.html',
	styleUrl: './logs.component.css'
})
export class LogsComponent implements OnInit, OnDestroy {

	/**
	 *  The header service.
	 */
	private readonly header = inject(MainService);

	/**
	 * The MOV APi service.
	 */
	private readonly mov = inject(MovApiService);

	/**
	 * Form builder service.
	 */
	private readonly fb = inject(FormBuilder);

	/**
	 * Dialog service.
	 */
	private readonly dialog = inject(MatDialog);

	/**
	 * Configuration service.
	 */
	private readonly conf = inject(ConfigService);

	/**
	 * The columns to display.
	 */
	public displayedColumns: string[] = ['timestamp', 'level', 'message', 'payload', 'componentType', 'componentName'];

	/**
	 * The component to manage the messages.
	 */
	public form: FormGroup = this.fb.group(
		{
			message: this.fb.control<string | null>(null),
			component: this.fb.control<string | null>(null),
			orderBy: this.fb.control<string>("timestamp"),
			reverse: this.fb.control<boolean>(true),
			levels: this.fb.control<string[]>([]),
			types: this.fb.control<string[]>([]),
		});

	/**
	 * The page to show.
	 */
	public page: LogRecordPage | null = null;

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
	 * The names of the log levels.
	 */
	public logNames = LOG_LEVEL_NAMES;

	/**
	 * The names of the component types.
	 */
	public componentTypeNames = COMPONENT_TYPE_NAMES;



	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the logs @@main_logs_code_page-title:Logs`);
		this.startUpdatePage();
		this.formChanged = this.form.valueChanges.subscribe(
			{
				next: () => this.startUpdatePage()
			}
		);

	}

	/**
	 * Called whne the component is destroyed.
	 */
	ngOnDestroy(): void {

		if (this.formChanged != null) {

			this.formChanged.unsubscribe();
			this.formChanged = null;
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

					if (!LogRecordPage.equals(this.page, page)) {

						this.page = page
					}
				}
			}
		);
	}

	/**
	 * Called to update the page.
	 */
	private getPage(): Observable<LogRecordPage> {

		var value = this.form.value;
		var pattern = toPattern(value.message);

		var component = toPattern(value.component);
		var type = toPattern(value.types);
		var orderBy = value.orderBy;
		if (value.reverse) {

			orderBy = "-" + orderBy;
		}
		var level = toPattern(value.levels);;
		var offset = this.pageIndex * this.pageSize;
		return this.mov.getLogRecordPage(pattern, level, component, type, orderBy, offset, this.pageSize);


	}

	/**
	 * Show the information of a payload.
	 */
	public showPayload(log: LogRecord) {

		this.dialog.open(ShowLogDialog, {
			data: log
		});

	}

}
