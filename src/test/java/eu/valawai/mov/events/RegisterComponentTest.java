/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link RegisterComponent}.
 *
 * @see RegisterComponent
 *
 * @author VALAWAI
 */
@QuarkusTest
public class RegisterComponentTest extends MovEventTestCase {

//	/**
//	 * Check that cannot register with an invalid payload.
//	 */
//	@Test
//	public void shouldNotRegisterComponentWithInvalidPayload() {
//
//		final var payload = new RegisterComponentPayload();
//		final var countLogs = this.logs.count() + 1;
//		final var countComponents = this.components.count();
//		this.assertPublish("valawai/component/register", payload);
//		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
//		assertEquals(countComponents, this.components.count());
//
//		final var log = this.logs.last();
//		assertEquals(LogLevel.ERROR, log.level);
//		assertEquals(JsonObject.mapFrom(payload).encodePrettily(), log.payload);
//
//	}
//
//	/**
//	 * Check that the not register with an invalid AsyncAPI.
//	 */
//	@Test
//	public void shouldNotRegisterComponentWithInvalidAsyncAPI() {
//
//		final var payload = new RegisterComponentPayloadTest().nextPayload();
//		payload.asyncapiYaml += "channels:\n\tBad:\n\ttype: string";
//		final var countLogs = this.logs.count() + 1;
//		final var countComponents = this.components.count();
//		this.assertPublish("valawai/component/register", payload);
//		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
//		assertEquals(countComponents, this.components.count());
//
//		final var log = this.logs.last();
//		assertEquals(LogLevel.ERROR, log.level);
//		assertEquals(JsonObject.mapFrom(payload).encodePrettily(), log.payload);
//
//	}
//
//	/**
//	 * Check that the user register a component.
//	 */
//	@Test
//	public void shouldRegisterComponent() {
//
//		final var payload = new RegisterComponentPayloadTest().nextPayload();
//		final var countLogs = this.logs.count() + 1;
//		final var countComponents = this.components.count();
//		this.assertPublish("valawai/component/register", payload);
//		this.wainUntilLog(countLogs, Duration.ofSeconds(30));
//		assertEquals(countComponents + 1, this.components.count());
//
//	}

}
