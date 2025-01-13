/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component } from '@angular/core';

import { RouterLink } from '@angular/router';

@Component({
	standalone: true,
    selector: 'app-not-found',
    imports: [RouterLink],
    templateUrl: './not-found.component.html',
    styleUrls: ['./not-found.component.css']
})
export class NotFoundComponent {

	constructor() { }

}
