/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class MainService {

	/**
	 * This emits the header titles.
	 */
	private subject = new Subject<string>();


	constructor() {

		this.subject.next("");
	}

	/**
	 * Listen for the changes for the header title.
	 */
	public headerTitle(): Observable<string> {

		return this.subject.asObservable();
	}

	/**
	 * Change the header title.
	 */
	public changeHeaderTitle(title: string) {

		setTimeout(() =>this.subject.next(title));

	}

}
