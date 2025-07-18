/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Directive, Injectable, Input } from '@angular/core';
import { StatusConnection } from './status-connection.model';
import { StatusNode } from './status-node.model';
import { StatusEndpoint } from './status-endpoint.model';
import { LiveTopologyComponentOutConnection } from '@app/shared/mov-api';

/**
 * This compony show a graph with the current status of the topology managed by the MOV.
 */
@Directive()
export abstract class AbstractConnectionDetailComponent {

	/**
	 * The connection to show the detail.
	 */
	protected _connection: StatusConnection | null = null;

	/**
	 * The nodes defined in the graph.
	 */
	protected _nodes: StatusNode[] = [];

	/**
	 * The source node.
	 */
	private source: StatusNode | null = null;

	/**
	 * The endpoint of the source used in the connection.
	 */
	private sourceEndPoint: StatusEndpoint | null = null;

	/**
	 * The target node.
	 */
	private target: StatusNode | null = null;

	/**
	 * The endpoint of the target used in the connection.
	 */
	private targetEndPoint: StatusEndpoint | null = null;


	/**
	 * Set the connection to show the detail.
	 */
	@Input()
	public set connection(connection: StatusConnection) {

		this._connection = connection;
		this.updateData();

	}

	/**
	 * Set the nodes of the graph.
	 */
	@Input()
	public set nodes(nodes: StatusNode[]) {

		this._nodes = nodes;
		this.updateData();

	}

	/**
	 * Obtain the data to show on the details.
	 */
	protected updateData() {

		if (this._nodes.length > 0 && this._connection != null) {

			this.updateDataWith(this._connection.model!);
		}
	}

	/**
	 * Obtain the data to show on the details.
	 */
	protected updateDataWith(connection: LiveTopologyComponentOutConnection) {

		this.source = this._nodes.find(node => node.searchLiveTopologyComponentOutConnectionWith(connection.id!) != null)!;
		this.sourceEndPoint = this.source.searchEndpointByChannel(connection.channel!)!;
		this.target = this._nodes.find(node => node.id == connection.target!.id)!;
		this.targetEndPoint = this.target.searchEndpointByChannel(connection.target!.channel!)!;
		
	}

	/**
	 * Return the connectin identifier.
	 */
	public get connectionId(): string {

		return this._connection?.model.id || '';

	}

	/**
	 * Return the source identifier.
	 */
	public get sourceId(): string {

		return this.source?.id || '';

	}

	/**
	 * Return the source name.
	 */
	public get sourceName(): string {

		return this.source?.name || '';

	}

	/**
	 * Return the source channel.
	 */
	public get sourceChannel(): string {

		return this.sourceEndPoint?.name || this._connection?.model?.channel || '';

	}

	/**
	 * Return the target identifier.
	 */
	public get targetId(): string {

		return this.target?.id || '';

	}

	/**
	 * Return the target name.
	 */
	public get targetName(): string {

		return this.target?.name || '';

	}

	/**
	 * Return the target channel.
	 */
	public get targetChannel(): string {

		return this.targetEndPoint?.name || this._connection?.model?.target?.channel || '';

	}

	/**
	 * Return if teh connection is eanbled:
	 */
	public get enabled(): boolean {

		return this._connection?.enabled || false;
	}

}
