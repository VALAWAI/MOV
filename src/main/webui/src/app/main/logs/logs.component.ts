/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Subscription } from 'rxjs';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { COMPONENT_TYPE_NAMES, LOG_LEVEL_NAMES, LogRecord, LogRecordPage, MovApiService } from 'src/app/shared/mov-api';
import { ShowLogDialog } from './show-log.dialog';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { NgFor, NgIf } from '@angular/common';
import { TimestampPipe } from '@app/shared/timestamp';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { ComponentNameBeautifier } from '@app/shared/component/view';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';

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
		NgIf,
		TimestampPipe,
		MatButton,
		MatIcon,
		ComponentNameBeautifier,
		MatPaginator,
		MatCheckbox,
		NgFor,
		MatTableModule
	],
	templateUrl: './logs.component.html',
	styleUrl: './logs.component.css'
})
export class LogsComponent implements OnInit, OnDestroy {

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
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private messages: MessagesService,
		private fb: FormBuilder,
		private dialog: MatDialog
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the logs @@main_logs_code_page-title:Logs`);
		this.updatePage();
		this.formChanged = this.form.valueChanges.subscribe(
			{
				next: () => this.updatePage()
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

	}

	/**
	 * Called when the page is changed.
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
		var pattern = value.message;
		if (pattern != null) {

			pattern = pattern.trim();
			if (pattern.length == 0) {

				pattern = null;

			} else {

				pattern = pattern.replace(/\*/, ".*");
				pattern = "/.*" + pattern + ".*/i";
			}

		}

		var component = value.component;
		if (component != null) {

			component = component.trim();
			if (component.length == 0) {

				component = null;

			} else {

				component = component.replace(/\*/, ".*");
				component = "/.*" + component + ".*/i";
			}

		}
		var type = null;
		if (value.types != null && value.types.length > 0) {

			type = value.types[0];
			if (value.types.length > 1) {

				type = "/" + type;
				for (var i = 1; i < value.types.length; i++) {

					type += "|" + value.types[i];
				}

				type += "/";

			}
		}
		var orderBy = value.orderBy;
		if (value.reverse) {

			orderBy = "-" + orderBy;
		}
		var level = null;
		if (value.levels != null && value.levels.length > 0) {

			level = value.levels[0];
			if (value.levels.length > 1) {

				level = "/" + level;
				for (var i = 1; i < value.levels.length; i++) {

					level += "|" + value.levels[i];
				}

				level += "/";

			}
		}
		var offset = this.pageIndex * this.pageSize;
		this.mov.getLogRecordPage(pattern, level, component, type, orderBy, offset, this.pageSize).subscribe(
			{
				next: page => this.page = page,
				error: err => {

					this.messages.showError($localize`:The error message whne can not get the logs @@main_logs_code_get-error:Cannot obtain the logs`);
					console.error(err);
				}
			}
		);


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
