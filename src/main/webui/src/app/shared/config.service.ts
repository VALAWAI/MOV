/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * This sercice is used to manage the configuration of the MOV.
 */
@Injectable({
	providedIn: 'root'
})
export class ConfigService {

	/**
	 * The subject to notify the changes on the polling time.
	 */
	private pollingTimeSubject: BehaviorSubject<number>;

	/**
	 * The subject to notify the changes on the editor show grid.
	 */
	private editorShowGridSubject: BehaviorSubject<boolean>;


	/**
	 * Create the service.
	 */
	constructor() {

		this.pollingTimeSubject = new BehaviorSubject<number>(this.pollingTime);
		this.editorShowGridSubject = new BehaviorSubject<boolean>(this.editorShowGrid);
	}

	/**
	 * Return the observable of the polling time. 
	 */
	public get pollingTime$(): Observable<number> {

		return this.pollingTimeSubject.asObservable();
	}

	public get pollingTime(): number {

		var pool = localStorage.getItem('POLLING_TIME');
		if (pool != null) {

			var time = Number(pool);
			if (!isNaN(time)) {

				return time;
			}
		}
		return 1500;
	}

	/**
	 * Store the polling time.
	 */
	public set pollingTime(time: number) {

		localStorage.setItem('POLLING_TIME', String(time));
		this.pollingTimeSubject.next(time);
	}

	/**
	 * Return the observable of the editor show grid. 
	 */
	public get editorShowGrid$(): Observable<boolean> {

		return this.editorShowGridSubject.asObservable();
	}

	public get editorShowGrid(): boolean {

		var item = localStorage.getItem('EDITOR_SHOW_GRID');
		console.log(item);
		return (item == null) || (item.toLowerCase() === 'true');
	}

	/**
	 * Store the editor show grid.
	 */
	public set editorShowGrid(view: boolean) {

		localStorage.setItem('EDITOR_SHOW_GRID', String(view));
		this.editorShowGridSubject.next(view);

	}

}
