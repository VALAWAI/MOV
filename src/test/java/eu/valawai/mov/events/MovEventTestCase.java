/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.valawai.mov.MasterOfValawaiTestCase;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;

/**
 * Generic test over the components that manage an event.
 *
 * @author VALAWAI
 */
@QuarkusTestResource(RabbitMQTestResource.class)
public class MovEventTestCase extends MasterOfValawaiTestCase {

	/**
	 * Service to send messages to the message broker.
	 */
	@Inject
	PublishService publish;

	/**
	 * Check that publish the specified payload.
	 *
	 * @param channelName name of the channel to publish a message.
	 * @param payload     of the message to publish.
	 */
	protected <P extends Payload> void assertPublish(String channelName, P payload) {

		assertTrue(this.publish.send(channelName, payload), "Cannot publish message");

	}

}
