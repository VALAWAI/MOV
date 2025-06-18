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
		emailSensor.repository = new GitHubRepository();
		emailSensor.repository.html_url = "https://github.com/VALAWAI/C0_email_sensor";
		this.assertItemNotNull(emailSensor.persist());

		final var now = TimeManager.now();
		this.assertItemIsNull(this.service.update());

		final var updatedEmailSensor = this.waitUntil(
				() -> (ComponentDefinitionEntity) this
						.assertItemNotNull(ComponentDefinitionEntity.findById(emailSensor.id)),
				component -> component.updatedAt >= now);
		assertEquals(emailSensor.type, updatedEmailSensor.type);
		assertNotEquals(emailSensor.name, updatedEmailSensor.name);
		assertEquals("E-mail sensor", updatedEmailSensor.name);
		assertNotNull(updatedEmailSensor.description);
		assertNotNull(updatedEmailSensor.repository);
		assertEquals("https://github.com/VALAWAI/C0_email_sensor", updatedEmailSensor.repository.html_url);
		assertEquals("https://valawai.github.io/docs/components/C0/email_sensor", updatedEmailSensor.docsLink);
		assertNotNull(updatedEmailSensor.version);
		assertNotNull(updatedEmailSensor.version.name);
		assertNotNull(updatedEmailSensor.version.since);
		assertNotNull(updatedEmailSensor.apiVersion);
		assertNotNull(updatedEmailSensor.apiVersion.name);
		assertNotNull(updatedEmailSensor.apiVersion.since);
		assertNotNull(updatedEmailSensor.channels);

		final var updatedEmailActuator = this.waitUntil(
				() -> (ComponentDefinitionEntity) this.assertNotFailure(ComponentDefinitionEntity.mongoCollection()
						.find(Filters.and(Filters.eq("type", ComponentType.C0.name()),
								Filters.eq("name", "E-mail actuator"), Filters.exists("updatedAt", true),
								Filters.ne("updatedAt", null), Filters.gte("updatedAt", now)))
						.collect().first()),
				component -> component != null, Duration.ofSeconds(1), Duration.ofMinutes(3));
		assertNotNull(updatedEmailActuator.description);
		assertNotNull(updatedEmailActuator.repository);
		assertEquals("https://github.com/VALAWAI/C0_email_actuator", updatedEmailActuator.repository.html_url);
		assertEquals("https://valawai.github.io/docs/components/C0/email_actuator", updatedEmailActuator.docsLink);
		assertNotNull(updatedEmailActuator.version);
		assertNotNull(updatedEmailActuator.version.name);
		assertNotNull(updatedEmailActuator.version.since);
		assertNotNull(updatedEmailActuator.apiVersion);
		assertNotNull(updatedEmailActuator.apiVersion.name);
		assertNotNull(updatedEmailActuator.apiVersion.since);
		assertNotNull(updatedEmailActuator.channels);

	}

}
