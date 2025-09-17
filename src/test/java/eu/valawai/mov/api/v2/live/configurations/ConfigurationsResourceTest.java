/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.configurations;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import eu.valawai.mov.MOVConfiguration;
import eu.valawai.mov.MOVConfiguration.TopologyBehavior;
import eu.valawai.mov.ValueGenerator;
import eu.valawai.mov.api.APITestCase;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntities;
import eu.valawai.mov.services.LocalConfigService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link ConfigurationsResource}.
 *
 * @see ConfigurationsResource
 *
 * @author VALAWAI
 */
@QuarkusTest
public class ConfigurationsResourceTest extends APITestCase {

	/**
	 * The local configuration of the MOV.
	 */
	@Inject
	LocalConfigService currentConfiguration;

	/**
	 * Should not set a bad topology identifier in the configuration.
	 */
	@Test
	public void shouldNotSetBadTopologyId() {

		given().contentType(ContentType.JSON).body(new Document("topologyId", 3)).when().put("/v2/life/configurations")
				.then().statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not set a bad register component in the configuration.
	 */
	@Test
	public void shouldNotSetBadRegisterComponent() {

		given().contentType(ContentType.JSON).body(new Document("registerComponent", "undefined")).when()
				.put("/v2/life/configurations").then().statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not set a bad create connection in the configuration.
	 */
	@Test
	public void shouldNotSetBadCreateConnection() {

		given().contentType(ContentType.JSON).body(new Document("createConnection", "undefined")).when()
				.put("/v2/life/configurations").then().statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should not set a bad configuration.
	 */
	@Test
	public void shouldNotSetUndefinedTopologyId() {

		final var configuration = new LiveConfigurationTest().nextModel();
		configuration.topologyId = TopologyGraphEntities.undefined();
		given().contentType(ContentType.JSON).body(configuration).when().put("/v2/life/configurations").then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());

	}

	/**
	 * Should set only the topology id.
	 */
	@Test
	public void shouldSetTopologyId() {

		final var configuration = new LiveConfiguration();
		configuration.topologyId = TopologyGraphEntities.minTopologies(1).get(0).id;
		final var updated = given().contentType(ContentType.JSON).body(configuration).when()
				.put("/v2/life/configurations").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LiveConfiguration.class);
		assertThat(updated.topologyId, is(configuration.topologyId));
		assertThat(updated.topologyId, is(this.currentConfiguration.getTopologyId()));
		assertThat(updated.createConnection,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME)));
		assertThat(updated.registerComponent,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME)));

	}

	/**
	 * Should set the create connection behaviour.
	 */
	@Test
	public void shouldSetCreateConnectionBehaviour() {

		this.currentConfiguration.setPropertyAsync(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME, null);

		final var configuration = new LiveConfiguration();
		configuration.createConnection = ValueGenerator.next(TopologyBehavior.values());

		final var updated = given().contentType(ContentType.JSON).body(configuration).when()
				.put("/v2/life/configurations").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LiveConfiguration.class);
		assertThat(updated.topologyId, is(nullValue()));
		assertThat(updated.createConnection, is(configuration.createConnection));
		assertThat(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME),
				is(configuration.createConnection));
		assertThat(updated.registerComponent,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME)));

	}

	/**
	 * Should set the register component behaviour.
	 */
	@Test
	public void shouldSetRegisterComponentBehaviour() {

		this.currentConfiguration.setPropertyAsync(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME, null);

		final var configuration = new LiveConfiguration();
		configuration.registerComponent = ValueGenerator.next(TopologyBehavior.values());

		final var updated = given().contentType(ContentType.JSON).body(configuration).when()
				.put("/v2/life/configurations").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LiveConfiguration.class);
		assertThat(updated.topologyId, is(nullValue()));
		assertThat(updated.createConnection,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME)));
		assertThat(updated.registerComponent, is(configuration.registerComponent));
		assertThat(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME),
				is(configuration.registerComponent));

	}

	/**
	 * Should set configuration.
	 */
	@Test
	public void shouldSetConfiguration() {

		this.currentConfiguration.setPropertyAsync(MOVConfiguration.TOPOLOGY_ID_NAME, null);
		this.currentConfiguration.setPropertyAsync(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME, null);
		this.currentConfiguration.setPropertyAsync(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME, null);

		final var configuration = new LiveConfigurationTest().nextModel();
		configuration.topologyId = TopologyGraphEntities.minTopologies(1).get(0).id;

		final var updated = given().contentType(ContentType.JSON).body(configuration).when()
				.put("/v2/life/configurations").then().statusCode(Status.OK.getStatusCode()).extract()
				.as(LiveConfiguration.class);
		assertThat(updated, is(configuration));
		assertThat(updated.topologyId, is(this.currentConfiguration.getTopologyId()));
		assertThat(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME),
				is(configuration.createConnection));
		assertThat(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME),
				is(configuration.registerComponent));

	}

	/**
	 * Should get configuration.
	 */
	@Test
	public void shouldGetConfiguration() {

		final var liveConfiguration = given().when().get("/v2/life/configurations").then()
				.statusCode(Status.OK.getStatusCode()).extract().as(LiveConfiguration.class);
		assertThat(liveConfiguration.topologyId, is(this.currentConfiguration.getTopologyId()));
		assertThat(liveConfiguration.createConnection,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME)));
		assertThat(liveConfiguration.registerComponent,
				is(this.currentConfiguration.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME)));
	}

}
