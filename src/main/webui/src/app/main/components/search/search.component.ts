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
import { COMPONENT_TYPE_NAMES, MinComponentPage, MovApiService } from 'src/app/shared/mov-api';
import { MatCheckbox } from '@angular/material/checkbox';
import { RouterLink } from '@angular/router';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { NgFor, NgIf } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { pullingTime } from '@app/shared';

@Component({
	standalone: true,
	selector: 'app-components-search',
	imports: [
		NgIf,
		ReactiveFormsModule,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatInput,
		MatCheckbox,
		MatButton,
		RouterLink,
		MatIcon,
		MatPaginator,
		NgFor,
		MatTableModule,
		MatMenuModule,
		MessageComponent
	],
	templateUrl: './search.component.html',
	styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit, OnDestroy {

	/**
	 * The columns to display.
	 */
	public displayedColumns: string[] = ['type', 'name', 'description', 'actions'];

	/**
	 * The component to manage the messages.
	 */
	public form: FormGroup = this.fb.group(
		{
			pattern: this.fb.control<string | null>(null),
			orderBy: this.fb.control<string>("timestamp"),
			reverse: this.fb.control<boolean>(false),
			types: this.fb.control<string[]>([]),
		});

	/**
	 * The page to show.
	 */
	public page: MinComponentPage | null = null;

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
	 * The names of the component types.
	 */
	public componentTypeNames = COMPONENT_TYPE_NAMES;


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private fb: FormBuilder
	) {

	}

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the components @@main_components_code_page-title:Components`);
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
	public ngOnDestroy(): void {

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

		this.pageSubscription = timer(0, pullingTime()).pipe(
			switchMap(() => this.getPage()),
			retry()
		).subscribe(
			{
				next: page => {

					if (JSON.stringify(this.page) !== JSON.stringify(page)) {

						this.page = page
					}
				}
			}
		);
	}

	/**
	 * Called to update the page.
	 */
	private getPage(): Observable<MinComponentPage> {

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
		var offset = this.pageIndex * this.pageSize;
		return this.mov.getMinComponentPage(pattern, type, orderBy, null, null, offset, this.pageSize);

	}


}
