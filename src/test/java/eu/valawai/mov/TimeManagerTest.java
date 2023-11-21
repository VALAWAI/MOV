/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov;

import static eu.valawai.mov.ValueGenerator.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test the {@link TimeManager}.
 *
 * @see TimeManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class TimeManagerTest {

	/**
	 * Check get now.
	 */
	@Test
	public void shouldGetNow() {

		assertTrue(TimeManager.now() > 0);
		assertTrue(TimeManager.now() >= System.currentTimeMillis() / 1000);
	}

	/**
	 * Conversion of an instant.
	 */
	@Test
	public void shouldConvertFromInstant() {

		final var expected = rnd().nextInt();
		final var instant = TimeManager.fromTime(expected);
		assertNotNull(TimeManager.now() > 0);
		assertEquals(expected, TimeManager.toTime(instant));
	}

}
