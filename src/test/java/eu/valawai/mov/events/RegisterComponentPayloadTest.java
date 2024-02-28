/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
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
				  title: Account Service
				  version: {0}.{1}.{2}
				  description: This service is in charge of processing user signups
				channels:
				  user/signedup:
				    subscribe:
				      message:
				        payload:
				          type: object
				          properties:
				            displayName:
				              type: string
				              description: Name of the user
				            email:
				              type: string
				              format: email
				              description: Email of the user
				            since:
				              type: string
				              enum:
				                - {0}
				                - {1}
				                - {2}
				""", 3).trim().replaceAll("\\t", "");
	}

}
