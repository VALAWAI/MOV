/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { MainService } from './main.service';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AsyncPipe } from '@angular/common';

@Component({
	standalone: true,
    selector: 'app-main',
    imports: [
        RouterOutlet,
        MatIcon,
        MatMenu,
        MatMenuTrigger,
        MatMenuItem,
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
		private main: MainService
	) {

		this.headerTitle = this.main.headerTitle();
	}


}
