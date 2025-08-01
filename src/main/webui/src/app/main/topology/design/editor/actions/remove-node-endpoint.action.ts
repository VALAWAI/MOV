/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChannelSchema } from "@app/shared/mov-api";
import { EditorNode } from "../editor-node.model";
import { TopologyEditorService } from "../topology.service";
import { AbstractCompositeAction } from "./abstract-composite.action";
import { ChangeNodeAction } from "./change-node.action";
import { RemoveConnectionAction } from "./remove-connection.action";
import { EditorEndpoint } from "../editor-endpoint.model";

/**
 * An actin to remove a node.
 */
export class RemoveNodeEndpointAction extends AbstractCompositeAction implements ChangeNodeAction {

	/**
	 * Create the event with the new node to remove.
	 */
	constructor(private endpoint: EditorEndpoint) {

		super();
	}

	/**
	 * Return the  node identifier.
	 */
	public get nodeId(): string {

		return this.endpoint.nodeId;
	}

	/**
	 * Undo the remove.
	 */
	public override undo(service: TopologyEditorService): void {

		const node = service.getNodeWith(this.nodeId)!;
		node.searchEndpointOrCreate(this.endpoint.channel, this.endpoint.isSource);
		super.undo(service);
	}

	/**
	 * Remove the node. 
	 */
	public override redo(service: TopologyEditorService): void {

		const node = service.getNodeWith(this.nodeId)!;
		var index = node.searchEndpointIndex(this.endpoint.channel, this.endpoint.isSource);
		this.endpoint = node.endpoints.splice(index, 1)[0];

		if (this.actions.length > 0) {

			super.redo(service);

		} else {

			const connectionToRemove = service.connections.filter(c => c.source.id == this.endpoint.id || c.target.id == this.endpoint.id);
			connectionToRemove.forEach(c => this.addAndRedo(new RemoveConnectionAction(c.id), service));
		}
	}
}

