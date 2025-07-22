/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.ArrayList;

import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.ModelTestCase;
import eu.valawai.mov.persistence.design.topology.TopologyGraphConnectionType;
import eu.valawai.mov.persistence.design.topology.TopologyGraphNode;
import eu.valawai.mov.persistence.design.topology.TopologyGraphNodeOutputConnection;
import jakarta.validation.constraints.NotNull;

/**
 * Test the {@link TopologyConnection}.
 *
 * @see TopologyConnection
 *
 * @author VALAWAI
 */
public class TopologyConnectionTest extends ModelTestCase<TopologyConnection> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TopologyConnection createEmptyModel() {

		return new TopologyConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TopologyConnection model) {

		final var builder = new TopologyConnectionEndpointTest();
		model.source = builder.nextModel();
		model.target = builder.nextModel();
		model.convertCode = ValueGenerator.nextPattern("//Convert {0}");
		model.type = ValueGenerator.next(TopologyGraphConnectionType.values());

		final int max = ValueGenerator.rnd().nextInt(0, 10);
		if (max > 0) {

			model.notifications = new ArrayList<>(max);
			final var notificationBuilder = new TopologyConnectionNotificationTest();
			for (int i = 0; i < max; i++) {

				final var notification = notificationBuilder.nextModel();
				model.notifications.add(notification);
			}
		}
	}

	/**
	 * Create a connection from a {@link TopologyGraphNode} to another one.
	 *
	 * @param node   as source of the connection.
	 * @param output that define how the connection is made.
	 *
	 * @return the connection between the node that follow the output.
	 */
	public static TopologyConnection from(@NotNull TopologyGraphNode node,
			@NotNull TopologyGraphNodeOutputConnection output) {

		final var model = new TopologyConnection();
		model.source = new TopologyConnectionEndpoint();
		model.source.nodeTag = node.tag;
		model.source.channel = output.sourceChannel;
		model.target = new TopologyConnectionEndpoint();
		model.target.nodeTag = output.targetTag;
		model.target.channel = output.targetChannel;
		model.convertCode = output.convertCode;
		model.type = output.type;

		if (output.notificationX != null && output.notificationY != null) {

			model.notificationPosition = new Point();
			model.notificationPosition.x = output.notificationX;
			model.notificationPosition.y = output.notificationY;
		}

		if (output.notifications != null) {

			model.notifications = new ArrayList<>(output.notifications.size());
			for (final var outNotification : output.notifications) {

				final var notification = TopologyConnectionNotificationTest.from(outNotification);
				model.notifications.add(notification);
			}

		}
		return model;
	}

}
