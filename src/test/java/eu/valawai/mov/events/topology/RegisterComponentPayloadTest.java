/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.events.PayloadTestCase;
import eu.valawai.mov.events.components.RegisterComponentPayload;

/**
 * Test the {@link RegisterComponentPayload}.
 *
 * @see RegisterComponentPayload
 *
 * @author VALAWAI
 */
public class RegisterComponentPayloadTest extends PayloadTestCase<RegisterComponentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegisterComponentPayload createEmptyModel() {

		return new RegisterComponentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(RegisterComponentPayload payload) {

		payload.type = next(ComponentType.values());
		payload.name = payload.type.name().toLowerCase() + nextPattern("_test_{0}");
		payload.version = nextPattern("{0}.{1}.{2}", 3);
		payload.asyncapiYaml = nextPattern("""
				asyncapi: 2.6.0
				info:
				  title: Service {0}
				  version: {1}.{2}.{3}
				  description: This service is in charge of processing user signups
				channels:
				  valawai/test_in_{4}:
				    subscribe:
				      message:
				        payload:
				          type: object
				          properties:
				            field_{4}:
				              type: string
				  valawai/test_out_54}:
				    publish:
				      message:
				        payload:
				          type: object
				          properties:
				            field_{5}:
				              type: string
				""", 6).trim().replaceAll("\\t", "");
	}

}
