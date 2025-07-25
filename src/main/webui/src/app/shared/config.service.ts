/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Injectable } from '@angular/core';
import { EFConnectionType } from '@foblex/flow';
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
	 * The subject to notify the changes on the polling iterations.
	 */
	private pollingIterationsSubject: BehaviorSubject<number>;

	/**
	 * The subject to notify the changes on the editor if it has to autoload the last edited topology.
	 */
	private editorAutoloadLastTopologySubject: BehaviorSubject<boolean>;

	/**
	 * The subject to notify the changes whne the editor has stored a topology.
	 */
	private editorLastStoredTopologyIdSubject: BehaviorSubject<string | null>;

	/**
	 * The subject to notify the changes on the live show grid.
	 */
	private liveShowGridSubject: BehaviorSubject<boolean>;

	/**
	 * The type of edges in the live graph.
	 */
	private liveEdgeTypeSubject: BehaviorSubject<EFConnectionType>;

	/**
	 * The subject to notify the changes on the live graph maximum nodes to show.
	 */
	private liveMaxNodesSubject: BehaviorSubject<number>;

	/**
	 * The subject to notify the changes on the editor autosave time.
	 */
	private editorAutosaveTimeSubject: BehaviorSubject<number>;

	/**
	 * The subject to notify the changes on the editor max history.
	 */
	private editorMaxHistorySubject: BehaviorSubject<number>;

	/**
	 * Create the service.
	 */
	constructor() {

		this.pollingTimeSubject = new BehaviorSubject<number>(this.pollingTime);
		this.editorShowGridSubject = new BehaviorSubject<boolean>(this.editorShowGrid);
		this.pollingIterationsSubject = new BehaviorSubject<number>(this.pollingIterations);
		this.editorAutoloadLastTopologySubject = new BehaviorSubject<boolean>(this.editorAutoloadLastTopology);
		this.editorLastStoredTopologyIdSubject = new BehaviorSubject<string | null>(this.editorLastStoredTopologyId);
		this.liveShowGridSubject = new BehaviorSubject<boolean>(this.liveShowGrid);
		this.liveEdgeTypeSubject = new BehaviorSubject<EFConnectionType>(this.liveEdgeType);
		this.liveMaxNodesSubject = new BehaviorSubject<number>(this.liveMaxNodes);
		this.editorAutosaveTimeSubject = new BehaviorSubject<number>(this.editorAutosaveTime);
		this.editorMaxHistorySubject = new BehaviorSubject<number>(this.editorMaxHistory);
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
		return (item == null) || (item.toLowerCase() === 'true');
	}

	/**
	 * Store the editor show grid.
	 */
	public set editorShowGrid(view: boolean) {

		localStorage.setItem('EDITOR_SHOW_GRID', String(view));
		this.editorShowGridSubject.next(view);

	}

	/**
	 * Return the observable of the polling iterations. 
	 */
	public get pollingIterations$(): Observable<number> {

		return this.pollingIterationsSubject.asObservable();
	}

	public get pollingIterations(): number {

		var pool = localStorage.getItem('POLLING_ITERATIONS');
		if (pool != null) {

			var iterations = Number(pool);
			if (!isNaN(iterations)) {

				return iterations;
			}
		}
		return 10;
	}

	/**
	 * Store the polling iterations.
	 */
	public set pollingIterations(iterations: number) {

		localStorage.setItem('POLLING_ITERATIONS', String(iterations));
		this.pollingIterationsSubject.next(iterations);
	}

	/**
	 * Return the observable of the editor autoload last topology. 
	 */
	public get editorAutoloadLastTopology$(): Observable<boolean> {

		return this.editorAutoloadLastTopologySubject.asObservable();
	}

	/**
	 * Get the current value if the editor must autoload last topology.
	 */
	public get editorAutoloadLastTopology(): boolean {

		var item = localStorage.getItem('EDITOR_AUTOLOAD_LAST_TOPOLOGY');
		return (item == null) || (item.toLowerCase() === 'true');
	}

	/**
	 * Store the editor autoload last topology.
	 */
	public set editorAutoloadLastTopology(view: boolean) {

		localStorage.setItem('EDITOR_AUTOLOAD_LAST_TOPOLOGY', String(view));
		this.editorAutoloadLastTopologySubject.next(view);

	}

	/**
	 * Return the observable of the identifier of th last topology stored in the editor. 
	 */
	public get editorLastStoredTopologyId$(): Observable<string | null> {

		return this.editorLastStoredTopologyIdSubject.asObservable();
	}

	/**
	 * Get the current value if the identifier of th last topology stored in the editor.
	 */
	public get editorLastStoredTopologyId(): string | null {

		return localStorage.getItem('EDITOR_LAST_STORED__TOPOLOGY_ID');

	}

	/**
	 * Store the identifier of th last topology stored in the editor.
	 */
	public set editorLastStoredTopologyId(id: string | null) {

		if (id) {

			localStorage.setItem('EDITOR_LAST_STORED__TOPOLOGY_ID', id);

		} else {

			localStorage.removeItem('EDITOR_LAST_STORED__TOPOLOGY_ID');
		}
		this.editorLastStoredTopologyIdSubject.next(id);
	}

	/**
	 * Return the observable of the live show grid. 
	 */
	public get liveShowGrid$(): Observable<boolean> {

		return this.liveShowGridSubject.asObservable();
	}

	public get liveShowGrid(): boolean {

		var item = localStorage.getItem('LIVE_SHOW_GRID');
		return (item == null) || (item.toLowerCase() === 'true');
	}

	/**
	 * Store the live show grid.
	 */
	public set liveShowGrid(view: boolean) {

		localStorage.setItem('LIVE_SHOW_GRID', String(view));
		this.liveShowGridSubject.next(view);

	}

	/**
	 * Return the observable of the live edge type. 
	 */
	public get liveEdgeType$(): Observable<EFConnectionType> {

		return this.liveEdgeTypeSubject.asObservable();
	}

	public get liveEdgeType(): EFConnectionType {

		var item = localStorage.getItem('LIVE_EDGE_TYPE');
		if (item) {

			item = item.toLowerCase();
			if (item == EFConnectionType.SEGMENT) {

				return EFConnectionType.SEGMENT;

			} else if (item == EFConnectionType.STRAIGHT) {

				return EFConnectionType.STRAIGHT;
			}
		}

		return EFConnectionType.BEZIER;

	}

	/**
	 * Store the live edge type.
	 */
	public set liveEdgeType(type: EFConnectionType) {

		localStorage.setItem('LIVE_EDGE_TYPE', String(type));
		this.liveEdgeTypeSubject.next(type);

	}


	/**
	 * Return the observable of the live max nodes. 
	 */
	public get liveMaxNodes$(): Observable<number> {

		return this.liveMaxNodesSubject.asObservable();
	}

	public get liveMaxNodes(): number {

		var item = localStorage.getItem('LIVE_MAX_NODES');
		if (item != null) {

			var maxNodes = Number(item);
			if (!isNaN(maxNodes)) {

				return maxNodes;
			}
		}
		return 100000;

	}

	/**
	 * Store the live max nodes.
	 */
	public set liveMaxNodes(nodes: number) {

		localStorage.setItem('LIVE_MAX_NODES', String(nodes));
		this.liveMaxNodesSubject.next(nodes);

	}


	/**
	 * Return the observable of the editor autosave time. 
	 */
	public get editorAutosaveTime$(): Observable<number> {

		return this.editorAutosaveTimeSubject.asObservable();
	}

	/**
	 * Return the time in milliseconds that the editor must wait before autosave.
	 */
	public get editorAutosaveTime(): number {

		var item = localStorage.getItem('EDITOR_AUTOSAVE_TIME');
		if (item != null) {

			var time = Number(item);
			if (!isNaN(time)) {

				return time;
			}
		}
		return 7000;

	}

	/**
	 * Store the  editor autosave time.
	 */
	public set editorAutosaveTime(time: number) {

		localStorage.setItem('EDITOR_AUTOSAVE_TIME', String(time));
		this.editorAutosaveTimeSubject.next(time);

	}

	/**
	 * Return the observable of the editor max history. 
	 */
	public get editorMaxHistory$(): Observable<number> {

		return this.editorMaxHistorySubject.asObservable();
	}

	/**
	 * Return the maximum number of changes to store in the changes of the editor.
	 */
	public get editorMaxHistory(): number {

		var item = localStorage.getItem('EDITOR_MAX_HISTORY');
		if (item != null) {

			var max = Number(item);
			if (!isNaN(max)) {

				return max;
			}
		}
		return 100;

	}

	/**
	 * Store the  editor max history.
	 */
	public set editorMaxHistory(max: number) {

		localStorage.setItem('EDITOR_MAX_HISTORY', String(max));
		this.editorMaxHistorySubject.next(max);

	}

}
