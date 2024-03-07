/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.events.components.RegisterComponentPayload;
import eu.valawai.mov.events.components.RegisterComponentPayloadTest;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;

/**
 * Test the {@link PayloadService}.
 *
 * @see PayloadService
 *
 * @author VALAWAI
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

		assertNull(this.service.safeDecodeAndVerify(null, Payload.class));

	}

	/**
	 * Check not decode unvalid object.
	 */
	@Test
	public void shouldNotDecodeUnvalidObject() {

		assertNull(this.service.safeDecodeAndVerify(new JsonObject(), RegisterComponentPayload.class));

	}

	/**
	 * Check decode a valid object.
	 */
	@Test
	public void shouldDecodeValidObject() {

		final var expected = new RegisterComponentPayloadTest().nextModel();
		final var object = JsonObject.mapFrom(expected);
		assertEquals(expected, this.service.safeDecodeAndVerify(object, RegisterComponentPayload.class));

	}

}
