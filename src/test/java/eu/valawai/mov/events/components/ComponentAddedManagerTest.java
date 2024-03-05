/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.components;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.events.MovEventTestCase;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;

/**
 * Test the {@link ComponentAddedManager}.
 *
 * @see ComponentAddedManager
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ComponentAddedManagerTest extends MovEventTestCase {

	/**
	 * The component to send events.
	 */
	@Inject
	EventBus bus;

	/**
	 * Should not manage an undefined component.
	 */
	@Test
	public void shouldNotManageForUndefinedComponent() {

		final var msg = new ComponentPlayloadTest().nextModel();
		this.bus.send(ComponentAddedManager.EVENT_ADDRESS, msg);

	}

}
