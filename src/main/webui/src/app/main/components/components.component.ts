/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, OnInit } from '@angular/core';
import { MainService } from 'src/app/main';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { Subscription } from 'rxjs';
import { MessagesService } from 'src/app/shared/messages';
import { COMPONENT_TYPE_NAMES, MinComponentPage, MovApiService } from 'src/app/shared/mov-api';

@Component({
	selector: 'app-components',
	templateUrl: './components.component.html',
	styleUrls: ['./components.component.css']
})
export class ComponentsComponent implements OnInit {

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
		private messages: MessagesService,
		private fb: FormBuilder
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the components @@main_components_code_page-title:Components`);
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
		this.mov.getMinComponentPage(pattern, type, orderBy, null, null, offset, this.pageSize).subscribe(
			{
				next: page => this.page = page,
				error: err => {

					this.messages.showError($localize`:The error message whne can not get the components@@main_components_code_get-error:Cannot obtain the components`);
					console.error(err);
				}
			}
		);


	}


}
