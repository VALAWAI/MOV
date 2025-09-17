/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.live.configurations;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import eu.valawai.mov.MOVConfiguration;
import eu.valawai.mov.persistence.design.topology.TopologyGraphEntity;
import eu.valawai.mov.services.LocalConfigService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * The web services to change the configurations of the MOV instance.
 *
 * @author VALAWAI
 */
@Path("/v2/live/configurations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationsResource {

	/**
	 * The local configuration of the MOV.
	 */
	@Inject
	LocalConfigService configuration;

	/**
	 * Set the configuration for the MOV.
	 *
	 * @param conf the configuration to set.
	 *
	 * @return the current configuration of the MOV.
	 */
	@PUT
	@Operation(description = "Set the configuration for the MOV.")
	@APIResponse(responseCode = "200", description = "The changed configuration of the MOV.", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveConfiguration.class)) })
	@APIResponse(responseCode = "400", description = "If the configuration is not valid.")
	public Uni<Response> setConfiguration(
			@RequestBody(description = "The new configuration of the MOV.", required = true, content = @Content(schema = @Schema(implementation = LiveConfiguration.class))) @Valid LiveConfiguration conf) {

		Uni<Boolean> set = Uni.createFrom().item(true);
		if (conf.topologyId != null) {

			set = set.chain(updated -> {

				final Uni<TopologyGraphEntity> find = TopologyGraphEntity.findById(conf.topologyId);
				return find.chain(defined -> {

					if (defined == null) {

						return Uni.createFrom().item(false);

					} else {

						return this.configuration.setProperty(MOVConfiguration.TOPOLOGY_ID_NAME,
								conf.topologyId.toHexString());
					}
				});
			});

		} else {

			set = set.chain(updated -> this.configuration.setProperty(MOVConfiguration.TOPOLOGY_ID_NAME, null));

		}

		if (conf.createConnection != null) {

			set = set.chain(updated -> {

				if (!updated) {

					return Uni.createFrom().item(false);

				} else {

					return this.configuration.setProperty(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME,
							conf.createConnection.name());
				}
			});
		}

		if (conf.registerComponent != null) {

			set = set.chain(updated -> {

				if (!updated) {

					return Uni.createFrom().item(false);

				} else {

					return this.configuration.setProperty(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME,
							conf.registerComponent.name());
				}
			});
		}

		return set.onFailure().recoverWithItem(error -> {

			Log.errorv(error, "Error setting the configuration");
			return false;

		}).chain(updated -> {

			if (!updated) {

				return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
						.entity("The configuration could not be set, please check the values.").build());

			} else {

				return this.getConfiguration();

			}

		});

	}

	/**
	 * Get the configuration of the MOV.
	 *
	 * @return the MOV configuration.
	 */
	@GET
	@Operation(description = "Obtain the MOV configuration.")
	@APIResponse(responseCode = "200", description = "The current configuration of the MOV.", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LiveConfiguration.class)) })
	public Uni<Response> getConfiguration() {

		final var configuration = new LiveConfiguration();
		configuration.topologyId = this.configuration.getTopologyId();
		configuration.createConnection = this.configuration
				.getTopologyBehaviour(MOVConfiguration.EVENT_CREATE_CONNECTION_NAME);
		configuration.registerComponent = this.configuration
				.getTopologyBehaviour(MOVConfiguration.EVENT_REGISTER_COMPONENT_NAME);
		return Uni.createFrom().item(Response.ok(configuration).build());

	}

}
