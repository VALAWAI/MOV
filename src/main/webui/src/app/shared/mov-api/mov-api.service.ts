/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import { HttpParams, HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { Info } from './info.model';
import { LogRecordPage } from './logs/log-record-page.model';
import { MinComponentPage } from './components/min-component-page.model';
import { Component } from './components/component.model';
import { MinConnectionPage } from './topology/min-connection-page.model';
import { TopologyConnection } from './topology/topology-connection.model';
import { ConnectionToCreate } from './topology/connection-to-create.model';
import { ChangeConnection } from './topology/change-connection.model';
import { ComponentToRegister } from './components/component-to-register.model';


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

	/**
	 * Get some logs.
	 */
	public getLogRecordPage(pattern: string | null = null, level: string | null = null,
	componentPattern: string | null = null, componentType: string | null = null,
	 order: string | null = null, offset: number = 0, limit: number = 20): Observable<LogRecordPage> {

		var url = this.url('/v1/logs');
		return this.http.get<LogRecordPage>(url, this.optionsWithParams(
		  {
		    pattern: pattern,
		    level: level,
		    componentPattern: componentPattern,
		    componentType: componentType,
		    order: order,
		    offset: offset,
		    limit: limit
		  }));
	}

	/**
	 * Get some components.
	 */
	public getMinComponentPage(pattern: string | null = null, type: string | null = null, hasPublishChannel: boolean | null = null,
		hasSubscribeChannel: boolean | null = null,
		order: string | null = null, offset: number = 0, limit: number = 20): Observable<MinComponentPage> {

		var url = this.url('/v1/components');
		return this.http.get<MinComponentPage>(url, this.optionsWithParams({
			pattern: pattern, type: type,
			hasPublishChannel: hasPublishChannel,
			hasSubscribeChannel: hasSubscribeChannel,
			order: order, offset: offset, limit: limit
		}));
	}

	/**
	 * Get a component.
	 */
	public getComponent(id: string): Observable<Component> {

		var url = this.url('/v1/components', [id]);
		return this.http.get<Component>(url);
	}

	/**
	 * Unregister a component.
	 */
	public unregisterComponent(id: string): Observable<void> {

		var url = this.url('/v1/components', [id]);
		return this.http.delete<void>(url);
	}

	/**
	 * Get some connections.
	 */
	public getMinConnectionPage(pattern: string | null = null, component: string | null = null, order: string | null = null, offset: number = 0, limit: number = 20): Observable<MinConnectionPage> {

		var url = this.url('/v1/topology/connections');
		return this.http.get<MinConnectionPage>(url, this.optionsWithParams({ pattern: pattern, component: component, order: order, offset: offset, limit: limit }));
	}

	/**
	 * Get a component.
	 */
	public getTopologyConnection(id: string): Observable<TopologyConnection> {

		var url = this.url('/v1/topology/connections', [id]);
		return this.http.get<TopologyConnection>(url);
	}

	/**
	 * Create a topology connection.
	 */
	public createTopologyConnection(create: ConnectionToCreate): Observable<void> {

		var url = this.url('/v1/topology/connections');
		return this.http.post<void>(url, create);
	}

	/**
	 * Change a topology connection.
	 */
	public updateTopologyConnection(change: ChangeConnection): Observable<void> {

		var url = this.url('/v1/topology/connections/change');
		return this.http.put<void>(url, change);
	}

	/**
	 * Register a component.
	 */
	public registerComponent(component: ComponentToRegister): Observable<void> {

		var url = this.url('/v1/components');
		return this.http.post<void>(url, component);
	}


}
