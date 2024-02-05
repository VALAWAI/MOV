/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import { HttpParams, HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { Info } from './info.model';


/**
 * The service to manage the students.
 */
@Injectable({
	providedIn: 'root'
})
export class MovApiService {


	/**
	 * Create the service.
	 */
	constructor(
		private http: HttpClient
	) {

	}

	/**
	 * Return the URL to the specified path.
	 */
	protected url(path: string, params: any[] | null = null): string {

		let url: string = environment.movUrl + path;
		if (params) {

			for (let param of params) {

				if (!url.endsWith('/')) {

					url += '/';
				}
				url += encodeURIComponent(param);
			}

		}

		return url;
	}

	/**
	 * Return the option to a HTTP with some parameters.
	 */
	protected optionsWithParams(params: any | null = null): object {

		if (params) {

			var httpParams = this.toHttpParams(params);
			return { params: httpParams };

		} else {

			return {};
		}
	}

	/**
	 * Convert an object to HTTP params.
	 */
	private toHttpParams(params: any) {

		var httpParams = new HttpParams();
		for (let key of Object.keys(params)) {

			if (
				typeof params[key] === 'string' || params[key] instanceof String
				|| typeof params[key] === 'boolean' || params[key] instanceof Boolean
				|| typeof params[key] === 'number' || params[key] instanceof Number
			) {

				httpParams = httpParams.append(key, params[key]);
			}
		}
		return httpParams;

	}

	/**
	 * Get the help information.
	 */
	public getHelp(): Observable<Info> {

		var url = this.url('/v1/help/info');
		return this.http.get<Info>(url);
	}
}
