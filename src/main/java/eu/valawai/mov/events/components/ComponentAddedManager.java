/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import eu.valawai.mov.persistence.components.ComponentEntity;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The manager to actuate when a new component has been added.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class ComponentAddedManager {

	/**
	 * The address to the events to this executor.
	 */
	public static final String EVENT_ADDRESS = "eu.valawai.mov.events.components.ComponentAddedManager";

	/**
	 * Component the specified message.
	 *
	 * @param msg that has been received.
	 */
	@ConsumeEvent(value = EVENT_ADDRESS)
	public void receivedMessage(final Message<ComponentPlayload> msg) {

		Log.tracev("Received message {0}", msg);
		final var body = msg.body();
		final Uni<ComponentEntity> find = ComponentEntity.findById(body.componentId);
		find.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Cannot obtain the component {0}", body.componentId);
			return null;

		}).subscribe().with(component -> {

			if (component != null && component.channels != null) {

				for (final var channel : component.channels) {

					if (channel.publish != null) {

						// channel.publish.type
					}

				}
			}

		});

	}

}
