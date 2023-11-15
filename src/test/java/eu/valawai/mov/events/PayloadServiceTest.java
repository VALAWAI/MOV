/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link PayloadService}.
 *
 * @see PayloadService
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
public class PayloadServiceTest extends MovEventTestCase {

	/**
	 * The service to test.
	 */
	@Inject
	PayloadService service;

	/**
	 * Check not decode {@code null} object.
	 */
	@Test
	public void shouldNotDecodeNullObject() {

		assertNull(this.service.decodeAndVerify(null, Payload.class));

	}

	/**
	 * Check not decode unvalid object.
	 */
	@Test
	public void shouldNotDecodeUnvalidObject() {

		assertNull(this.service.decodeAndVerify(new JsonObject(), RegisterComponentPayload.class));

	}

	/**
	 * Check decode a valid object.
	 */
	@Test
	public void shouldDecodeValidObject() {

		final var expected = new RegisterComponentPayloadTest().nextPayload();
		final var object = JsonObject.mapFrom(expected);
		assertEquals(expected, this.service.decodeAndVerify(object, RegisterComponentPayload.class));

	}

}
