/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.mongodb.client.model.Filters;

import eu.valawai.mov.MOVConfiguration.ComponentStartupMode;
import eu.valawai.mov.MOVConfiguration.ConnectionStartupMode;
import eu.valawai.mov.persistence.live.components.ComponentEntities;
import eu.valawai.mov.persistence.live.components.ComponentEntity;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntities;
import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkiverse.quinoa.testing.QuinoaTestProfiles;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

/**
 * Test the {@link OnStart}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@QuarkusTest
@TestProfile(QuinoaTestProfiles.Enable.class)
public class OnStartTest extends MasterOfValawaiTestCase {

	/**
	 * The configuration of the MOV.
	 */
	@Inject
	MOVConfiguration conf;

	/**
	 * Test not get undefined index.
	 */
	@Test
	public void shouldNotGetUndefinedIndex() {

		given().accept(ContentType.HTML).when().get("/nz/index.html").then()
				.statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Test get environment from locale.
	 */
	@Test
	public void shouldGetEnvironmentFromLanguage() {

		given().when().get("/nz/env.js").then().statusCode(Status.OK.getStatusCode());

	}

	/**
	 * Test not delete an undefined treatment.
	 */
	@Test
	public void shouldNotGetIndexIfNotDefineHTmlMimeType() {

		given().when().get("/index.html").then().statusCode(Status.NOT_FOUND.getStatusCode());

	}

	/**
	 * Test that drop the previous components
	 */
	@Test
	public void shouldDropPreviousComponents() {

		ComponentEntities.minComponents(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.components()).thenReturn(ComponentStartupMode.DROP);
		start.handle(null);

		final var collectionNames = ComponentEntity.mongoDatabase().listCollectionNames().collect().asList().subscribe()
				.withSubscriber(UniAssertSubscriber.create()).awaitItem(Duration.ofSeconds(30)).getItem();
		assertFalse(collectionNames.contains(ComponentEntity.COLLECTION_NAME));

	}

	/**
	 * Test that finish the previous components
	 */
	@Test
	public void shouldFinishPreviousComponents() {

		ComponentEntities.minComponents(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.components()).thenReturn(ComponentStartupMode.FINISH);
		start.handle(null);

		ComponentEntity.mongoCollection()
				.countDocuments(Filters.or(Filters.exists("finishedTime", false), Filters.eq("finishedTime", null)))
				.subscribe().withSubscriber(UniAssertSubscriber.create()).awaitItem(Duration.ofSeconds(30))
				.assertItem(0l);

	}

	/**
	 * Test that preserve the previous components
	 */
	@Test
	public void shouldPreservePreviousComponents() {

		final var components = ComponentEntities.minComponents(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.components()).thenReturn(ComponentStartupMode.PRESERVE);
		start.handle(null);

		for (final var component : components) {

			final Uni<ComponentEntity> find = ComponentEntity.findById(component.id);
			final var updated = find.subscribe().withSubscriber(UniAssertSubscriber.create())
					.awaitItem(Duration.ofSeconds(30)).getItem();
			assertNotNull(updated);
			assertEquals(component.finishedTime, updated.finishedTime);

		}

	}

	/**
	 * Test that drop the previous connections
	 */
	@Test
	public void shouldDropPreviousConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.connections()).thenReturn(ConnectionStartupMode.DROP);
		start.handle(null);

		final var collectionNames = TopologyConnectionEntity.mongoDatabase().listCollectionNames().collect().asList()
				.subscribe().withSubscriber(UniAssertSubscriber.create()).awaitItem(Duration.ofSeconds(30)).getItem();
		assertFalse(collectionNames.contains(TopologyConnectionEntity.COLLECTION_NAME));

	}

	/**
	 * Test that delete the previous connections
	 */
	@Test
	public void shouldDeletePreviousConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.connections()).thenReturn(ConnectionStartupMode.DELETE);
		start.handle(null);

		TopologyConnectionEntity.mongoCollection()
				.countDocuments(
						Filters.or(Filters.exists("deletedTimestamp", false), Filters.eq("deletedTimestamp", null)))
				.subscribe().withSubscriber(UniAssertSubscriber.create()).awaitItem(Duration.ofSeconds(30))
				.assertItem(0l);

	}

	/**
	 * Test that disable the previous connections
	 */
	@Test
	public void shouldDisablePreviousConnections() {

		TopologyConnectionEntities.minTopologyConnections(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.connections()).thenReturn(ConnectionStartupMode.DISABLE);
		start.handle(null);

		TopologyConnectionEntity.mongoCollection()
				.countDocuments(Filters.or(Filters.exists("enabled", false), Filters.eq("enabled", true))).subscribe()
				.withSubscriber(UniAssertSubscriber.create()).awaitItem(Duration.ofSeconds(30)).assertItem(0l);

	}

	/**
	 * Test that preserve the previous connections
	 */
	@Test
	public void shouldPreservePreviousConnections() {

		final var connections = TopologyConnectionEntities.minTopologyConnections(100);
		final var start = new OnStart();
		final var init = Mockito.spy(this.conf.init());
		start.conf = Mockito.spy(this.conf);
		Mockito.when(start.conf.init()).thenReturn(init);
		Mockito.when(init.connections()).thenReturn(ConnectionStartupMode.PRESERVE);
		start.handle(null);

		for (final var connection : connections) {

			final Uni<TopologyConnectionEntity> find = TopologyConnectionEntity.findById(connection.id);
			final var updated = find.subscribe().withSubscriber(UniAssertSubscriber.create())
					.awaitItem(Duration.ofSeconds(30)).getItem();
			assertNotNull(updated);
			assertEquals(connection.deletedTimestamp, updated.deletedTimestamp);
			assertEquals(connection.enabled, updated.enabled);

		}

	}

}
