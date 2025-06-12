/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { MainService } from './main.service';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterOutlet, RouterLink } from '@angular/router';
import { AsyncPipe, CommonModule } from '@angular/common';
import { LOCALE_ID, Inject } from '@angular/core';

@Component({
	standalone: true,
	selector: 'app-main',
	imports: [
		CommonModule,
		RouterOutlet,
		MatIconModule,
		MatMenuModule,
		RouterLink,
		AsyncPipe
	],
	templateUrl: './main.component.html',
	styleUrl: './main.component.css'
})
export class MainComponent {

	/**
	 * The title for the header.
	 */
	public headerTitle: Observable<string>;


	/**
	 *  Create the component.
	 */
	constructor(
		private main: MainService,
		@Inject(LOCALE_ID) private locale: string
	) {

		this.headerTitle = this.main.headerTitle();
	}

	/**
	 * Change the locale of the application
	 */
	public changeLocaleTo(lang: string) {

		var path = '';
		var host = '';
		var href = window.location.href;
		var index = href.indexOf('/main/');
		if (index > 0) {

			path = href.substring(index);
			host = href.substring(0, index);
		}
		if (host.match(/\/[a-z]{2}$/)) {

			host = host.substring(0, host.length - 2);

		} else if (!host.endsWith('/')) {

			host = host + '/';
		}
		window.location.href = host + lang + path;

	}

	/**
	 * Check if the app is localized in a language.
	 */
	public isLocalizedIn(lang: string) {

		return this.locale != null && this.locale.indexOf(lang) > -1;
	}


}
