/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.v1.components.ComponentType;

/**
 * Test the {@link RegisterComponentPayload}.
 *
 * @see RegisterComponentPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class RegisterComponentPayloadTest extends PayloadTestCase<RegisterComponentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegisterComponentPayload createEmptyPayload() {

		return new RegisterComponentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegisterComponentPayload nextPayload() {

		final var payload = this.createEmptyPayload();
		payload.type = next(ComponentType.values());
		payload.name = payload.type.name().toLowerCase() + nextPattern("_test_{0}");
		payload.version = nextPattern("{0}.{1}.{2}", 3);
		payload.asyncapiYaml = nextPattern("""
				asyncapi: 2.6.0
				info:
				  title: Component API specification.
				  version: {0}.{1}.{2}
				channels:
				  valaway/c0/sensor:
				    publish:
				      message:
				        $ref: '#/components/messages/sensor_message'
				  valaway/c0/actuator:
				    subscribe:
				      message:
				        $ref: '#/components/messages/actuator_message'
				components:
				  messages:
				    sensor_message:
				      payload:
				        $ref: '#/components/schemas/actuator_message_payload'
				    actuator_message:
				      payload:
				        type: object
				        properties:
				          value:
				            type: number
				            enum:
				            	- {0}
				            	- {1}
				            	- {2}
				  schemas:
				    actuator_message_payload:
				    	type: object
				        properties:
				          value:
				            type: number
				          unit:
				            type: string
				            """, 3).trim();
		return payload;
	}

}
