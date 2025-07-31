/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ChannelSchema } from './components/channel-schema.model';
import { PayloadSchema } from './components/payload-schema.model';

export { MovApiService } from './mov-api.service';
export { Info } from './info.model';
export { HealthInfo, HealthStatus, HealthCheck } from './health-info.model';
export { LOG_LEVEL_NAMES, LogLevel } from './logs/log-level.model';
export { LogRecord } from './logs/log-record.model';
export { LogRecordPage } from './logs/log-record-page.model';
export { MinComponent } from './components/min-component.model';
export { MinComponentPage } from './components/min-component-page.model';
export { Component } from './components/component.model';
export { COMPONENT_TYPE_NAMES, ComponentType } from './components/component-type.model';
export { ChannelSchema } from './components/channel-schema.model';
export { PayloadSchema, PayloadType } from './components/payload-schema.model';
export { BasicPayloadFormat, BASIC_PAYLOAD_FORMAT_NAMES } from './components/basic-payload-format.model';
export { BasicPayloadSchema } from './components/basic-payload-schema.model';
export { EnumPayloadSchema } from './components/enum-payload-schema.model';
export { ObjectPayloadSchema } from './components/object-payload-schema.model';
export { ArrayPayloadSchema } from './components/array-payload-schema.model';
export { MinConnection } from './topology/min-connection.model';
export { MinConnectionPage } from './topology/min-connection-page.model';
export { TopologyConnection } from './topology/topology-connection.model';
export { TopologyConnectionNode } from './topology/topology-connection-node.model';
export { TOPOLOGY_ACTION_NAMES, TopologyAction } from './topology/topology-action.model';
export { ChangeConnection } from './topology/change-connection.model';
export { ConnectionToCreate } from './topology/connection-to-create.model';
export { ConstantPayloadSchema } from './components/constant-payload-schema.model';
export { ReferencePayloadSchema } from './components/reference-payload-schema.model';
export { OneOfPayloadSchema } from './components/one-of-payload-schema.model';
export { AnyOfPayloadSchema } from './components/any-of-payload-schema.model';
export { AllOfPayloadSchema } from './components/all-of-payload-schema.model';

export { VersionInfo } from './design/components/version-info.model';
export { VersionInfoToNamePipe } from './design/components/version-info.pipe';
export { ComponentDefinition } from './design/components/component-definition.model';
export { ComponentDefinitionPage } from './design/components/component-definition-page.model';
export { ComponentsLibraryStatus } from './design/components/components-library-status.model';

export { Point } from './design/topologies/point.model';
export { Topology } from './design/topologies/topology.model';
export { TopologyConnection as DesignTopologyConnection, type TopologyGraphConnectionType, toTopologyGraphConnectionType } from './design/topologies/topology-connection.model';
export { TopologyConnectionEndpoint } from './design/topologies/topology-connection-endpoint.model';
export { TopologyNode } from './design/topologies/topology-node.model';
export { MinTopology } from './design/topologies/min-topology.model';
export { MinTopologyPage } from './design/topologies/min-topology-page.model';

export { LiveTopology } from './live/topologies/live-topology.model';
export { LiveTopologyComponent } from './live/topologies/live-topology-component.model';
export { LiveTopologyComponentOutConnection } from './live/topologies/live-topology-component-out-connection.model';
export { LiveTopologyConnectionEndpoint } from './live/topologies/live-topology-connection-endpoint.model';


/**
 * Check if two payload schemas match.
 */
export function matchPayloadSchema(source: PayloadSchema | null | undefined, target: PayloadSchema | null | undefined): boolean {

	if (source == target) {

		return true;

	} else if (source == null || target == null) {

		return false;

	} else {

		return JSON.stringify(source) == JSON.stringify(target);
	}
}

/**
 * Sort the channles by name.
 */
export function sortChannelSchemaByName(channels: ChannelSchema[]): void {

	channels.sort((c1, c2) => {

		var source = '';
		if (c1.name != null) {

			source = c1.name;
		}
		var target = '';
		if (c2.name != null) {

			target = c2.name;
		}
		return source.localeCompare(target);
	});
}