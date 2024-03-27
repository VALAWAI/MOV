/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.next;
import static eu.valawai.mov.ValueGenerator.nextPattern;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Test the {@link ComponentToRegister}.
 *
 * @see ComponentToRegister
 *
 * @author VALAWAI
 */
public class ComponentToRegisterTest extends ModelTestCase<ComponentToRegister> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentToRegister createEmptyModel() {

		return new ComponentToRegister();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(ComponentToRegister model) {

		model.type = next(ComponentType.values());
		model.name = model.type.name().toLowerCase() + nextPattern("_test_{0}");
		model.version = nextPattern("{0}.{1}.{2}", 3);
		model.asyncapiYaml = nextPattern("""
				asyncapi: 2.6.0
				info:
				  title: Service {0}
				  version: {1}.{2}.{3}
				  description: This service is in charge of processing user sign ups
				channels:
				  valawai/test_in_{4}:
				    subscribe:
				      message:
				        model:
				          type: object
				          properties:
				            field_{4}:
				              type: string
				  valawai/test_out_54}:
				    publish:
				      message:
				        model:
				          type: object
				          properties:
				            field_{5}:
				              type: string
				""", 6).trim().replaceAll("\\t", "");
	}

}
