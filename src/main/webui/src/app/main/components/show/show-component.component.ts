/*
  Copyright 2022-2026 VALAWAY

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
	selector: 'app-showcomponent',
	templateUrl: './show-component.component.html',
	styleUrls: ['./show-component.component.css']
})
export class ShowComponentComponent implements OnInit {

	/**
	 * The indeitifer of teh component to show.
	 */
	public componentId: string | null = null;


	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private messages: MessagesService
	) {

	}

	/**
	 * Initialize the component.
	 */
	ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the show component @@main_show-component_code_page-title:Show Component`);

	}


}
