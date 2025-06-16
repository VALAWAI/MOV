/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.ValueGenerator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link LocalConfigService}.
 *
 * @see LocalConfigService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class LocalConfigServiceTest extends MasterOfValawaiTestCase {

	/**
	 * The service to manage the local configuration.
	 */
	@Inject
	LocalConfigService service;

	/**
	 * Check that can be set a property.
	 */
	@Test
	public void shouldSetProperty() {

		final var key = ValueGenerator.nextPattern("key_{0}");
		final var value = ValueGenerator.nextPattern("Value with number {0}");

		final var set = this.assertItemNotNull(this.service.setProperty(key, value));
		assertTrue(set, "Not set property");

		final var confValue = ConfigProvider.getConfig().getValue(key, String.class);
		assertEquals(confValue, value);

	}

}
