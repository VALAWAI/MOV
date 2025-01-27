/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.persistence.topology.TopologyConnectionEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

/**
 * This is a basic simulation of the components that form the treatment demo.
 *
 * @author VALAWAI
 */
@QuarkusTest
public class TreatementSimulationTest extends EndToEndTestCase {

	/**
	 * The test basic treatment demo.
	 */
	@Test
	public void shouldComunicteBetweenTreatemntDemoComponents() {

		final var c0RegisterPayload = this.createRegisterComponentPayloadForResource("c0_patient_treatment_ui_1.0.0");
		final var c0 = this.assertRegister(c0RegisterPayload);

		var c1 = this.registerC1(c0);
		var c2 = this.registerC2(c0);

		final var treatment = new JsonObject();
		treatment.put("id", UUID.randomUUID().toString());
		treatment.put("patient_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment));

		var receivedTreatment = c1.queues.get("valawai/c1/nit_protocol_manager/data/treatment").waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		receivedTreatment = c2.queues.get("valawai/c2/treatment_beneficence_valuator/data/treatment")
				.waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		final var feedback = new JsonObject();
		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c1/nit_protocol_manager/data/treatment_action_feedback", feedback));

		var receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_action_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(() -> this
				.assertPublish("valawai/c2/treatment_beneficence_valuator/data/treatment_value_feedback", feedback));

		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_value_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		// unregister c2
		this.assertUnregister(c2);

		treatment.put("patient_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment));

		receivedTreatment = c1.queues.get("valawai/c1/nit_protocol_manager/data/treatment").waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c1/nit_protocol_manager/data/treatment_action_feedback", feedback));

		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_action_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		// register again c2
		c2 = this.registerC2(c0);

		treatment.put("patient_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment));

		receivedTreatment = c1.queues.get("valawai/c1/nit_protocol_manager/data/treatment").waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		receivedTreatment = c2.queues.get("valawai/c2/treatment_beneficence_valuator/data/treatment")
				.waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c1/nit_protocol_manager/data/treatment_action_feedback", feedback));

		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_action_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(() -> this
				.assertPublish("valawai/c2/treatment_beneficence_valuator/data/treatment_value_feedback", feedback));

		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_value_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		// unregister c1
		this.assertUnregister(c1);

		treatment.put("patient_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment));

		receivedTreatment = c2.queues.get("valawai/c2/treatment_beneficence_valuator/data/treatment")
				.waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());

		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(() -> this
				.assertPublish("valawai/c2/treatment_beneficence_valuator/data/treatment_value_feedback", feedback));

		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_value_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

		// unregister c2
		this.assertUnregister(c2);

		treatment.put("patient_id", UUID.randomUUID().toString());
		this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment);

		c1 = this.registerC1(c0);
		c2 = this.registerC2(c0);

		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c0/patient_treatment_ui/data/treatment", treatment));
		receivedTreatment = c1.queues.get("valawai/c1/nit_protocol_manager/data/treatment").waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());
		receivedTreatment = c2.queues.get("valawai/c2/treatment_beneficence_valuator/data/treatment")
				.waitReceiveMessage();
		assertEquals(treatment.encode(), receivedTreatment.encode());
		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(
				() -> this.assertPublish("valawai/c1/nit_protocol_manager/data/treatment_action_feedback", feedback));
		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_action_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());
		feedback.put("treatement_id", UUID.randomUUID().toString());
		this.executeAndWaitUntilNewLog(() -> this
				.assertPublish("valawai/c2/treatment_beneficence_valuator/data/treatment_value_feedback", feedback));
		receivedFeedback = c0.queues.get("valawai/c0/patient_treatment_ui/data/treatment_value_feedback")
				.waitReceiveMessage();
		assertEquals(feedback.encode(), receivedFeedback.encode());

	}

	/**
	 * Register the C2 component.
	 *
	 * @param c0 component.
	 *
	 * @return the c2 component.
	 */
	private ComponentSimulator registerC2(ComponentSimulator c0) {

		final var c2RegisterPayload = this
				.createRegisterComponentPayloadForResource("c2_treatment_beneficence_valuator_1.0.2");
		final var c2 = this.assertRegister(c2RegisterPayload);

		// check C0 to C2 connections
		final var queryC0ToC2 = Filters.and(Filters.eq("source.componentId", c0.id),
				Filters.eq("source.channelName", "valawai/c0/patient_treatment_ui/data/treatment"),
				Filters.eq("target.componentId", c2.id),
				Filters.eq("target.channelName", "valawai/c2/treatment_beneficence_valuator/data/treatment"),
				Filters.eq("enabled", true));
		this.waitUntilNotNull(() -> TopologyConnectionEntity.count(queryC0ToC2), count -> count == 1);

		// check C2 to C0 connections
		final var queryC2ToC0 = Filters.and(Filters.eq("source.componentId", c2.id),
				Filters.eq("source.channelName",
						"valawai/c2/treatment_beneficence_valuator/data/treatment_value_feedback"),
				Filters.eq("target.componentId", c0.id),
				Filters.eq("target.channelName", "valawai/c0/patient_treatment_ui/data/treatment_value_feedback"),
				Filters.eq("enabled", true));
		this.waitUntilNotNull(() -> TopologyConnectionEntity.count(queryC2ToC0), count -> count == 1);
		return c2;

	}

	/**
	 * Register the C1 component.
	 *
	 * @param c0 component.
	 *
	 * @return the c1 component.
	 */
	private ComponentSimulator registerC1(ComponentSimulator c0) {

		final var c1RegisterPayload = this.createRegisterComponentPayloadForResource("c1_nit_protocol_manager_1.0.1");
		final var c1 = this.assertRegister(c1RegisterPayload);

		// check C0 to C1 connections
		final var queryC0ToC1 = Filters.and(Filters.eq("source.componentId", c0.id),
				Filters.eq("source.channelName", "valawai/c0/patient_treatment_ui/data/treatment"),
				Filters.eq("target.componentId", c1.id),
				Filters.eq("target.channelName", "valawai/c1/nit_protocol_manager/data/treatment"),
				Filters.eq("enabled", true));
		this.waitUntilNotNull(() -> TopologyConnectionEntity.count(queryC0ToC1), count -> count == 1);

		// check C1 to C0 connections
		final var queryC1ToC0 = Filters.and(Filters.eq("source.componentId", c1.id),
				Filters.eq("source.channelName", "valawai/c1/nit_protocol_manager/data/treatment_action_feedback"),
				Filters.eq("target.componentId", c0.id),
				Filters.eq("target.channelName", "valawai/c0/patient_treatment_ui/data/treatment_action_feedback"),
				Filters.eq("enabled", true));
		this.waitUntilNotNull(() -> TopologyConnectionEntity.count(queryC1ToC0), count -> count == 1);
		return c1;
	}

}
