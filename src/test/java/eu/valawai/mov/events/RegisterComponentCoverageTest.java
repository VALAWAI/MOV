/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test that do exceptional cases to increases the test coverage.
 *
 * @see RegisterComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RegisterComponentCoverageTest {

	/**
	 * The component to manage the messages.
	 */
	@Inject
	PayloadService service;

	/**
	 * Check that can register component because cannot add it.
	 */
	@Test
	public void shouldNotRegisterBecauseCannotAdd() {

//		final var manager = new RegisterComponent();
//		manager.service = this.service;
//		manager.components = Mockito.mock(ComponentRepository.class);
//		manager.logs = Mockito.mock(LogRecordRepository.class);
//		final var content = JsonObject.mapFrom(new RegisterComponentPayloadTest().nextModel());
//		doReturn(null).when(manager.components).add(any());
//		manager.consume(content);
//		verify(manager.components, times(1)).add(any());
//		verify(manager.logs, times(1)).add(any());

	}

}
