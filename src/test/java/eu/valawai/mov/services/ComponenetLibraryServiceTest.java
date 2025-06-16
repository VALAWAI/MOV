/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.components.ComponentType;
import eu.valawai.mov.persistence.design.component.ComponentDefinitionEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Test the {@link ComponenetLibraryService}.
 *
 * @see ComponenetLibraryService
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ComponenetLibraryServiceTest extends MasterOfValawaiTestCase {

	/**
	 * The service to test.
	 */
	@Inject
	ComponenetLibraryService service;

	/**
	 * Check that the components are updated.
	 */
	@Test
	public void shouldUpdateLibrary() {

		this.assertItemNotNull(ComponentDefinitionEntity.mongoCollection()
				.deleteMany(Filters.and(Filters.eq("type", "C0"), Filters.regex("name", ".*e.?mail.+", "i"))));

		final var emailSensor = new ComponentDefinitionEntity();
		emailSensor.type = ComponentType.C0;
		emailSensor.name = "Email sensor";
		this.assertItemNotNull(emailSensor.persist());

		final var now = TimeManager.now();
		this.assertItemIsNull(this.service.update());

		final var updatedEmailSensor = this.waitUntil(
				() -> (ComponentDefinitionEntity) this
						.assertItemNotNull(ComponentDefinitionEntity.findById(emailSensor.id)),
				component -> component.updatedAt != null && component.updatedAt >= now);
		assertEquals(emailSensor.type, updatedEmailSensor.type);
		assertNotEquals(emailSensor.name, updatedEmailSensor.name);
		assertEquals("E-mail sensor", updatedEmailSensor.name);
		assertNotNull(updatedEmailSensor.description);
		assertEquals("https://github.com/VALAWAI/C0_email_sensor", updatedEmailSensor.gitLink);
		assertEquals("https://valawai.github.io/docs/components/C0/email_sensor", updatedEmailSensor.docsLink);
		assertNotNull(updatedEmailSensor.version);
		assertNotNull(updatedEmailSensor.apiVersion);
		assertNotNull(updatedEmailSensor.channels);

		final var updatedEmailActuator = this.waitUntil(
				() -> (ComponentDefinitionEntity) this.assertNotFailure(ComponentDefinitionEntity.mongoCollection()
						.find(Filters.and(Filters.eq("type", ComponentType.C0.name()),
								Filters.regex("type", ".*e.?mail.+actuator.*", "i"), Filters.exists("updatedAt", true),
								Filters.ne("updatedAt", null), Filters.gte("updatedAt", now)))
						.collect().first()),
				component -> component == null, Duration.ofSeconds(1), Duration.ofMinutes(3));
		assertEquals("E-mail actuator", updatedEmailActuator.name);
		assertNotNull(updatedEmailActuator.description);
		assertEquals("https://github.com/VALAWAI/C0_email_actuator", updatedEmailActuator.gitLink);
		assertEquals("https://valawai.github.io/docs/components/C0/email_actuator", updatedEmailActuator.docsLink);
		assertNotNull(updatedEmailActuator.version);
		assertNotNull(updatedEmailActuator.apiVersion);
		assertNotNull(updatedEmailActuator.channels);

	}

}
