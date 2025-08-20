/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import java.util.Optional;

import org.bson.types.ObjectId;

import io.quarkus.arc.Unremovable;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;

/**
 * The configuration properties of the master of VALAWAI.
 *
 * @author VALAWAI
 */
@ConfigMapping(prefix = "mov")
@Unremovable
public interface MOVConfiguration {

	/**
	 * Obtain the configuration when the MOV starts.
	 *
	 * @return the configuration to apply when the MOV is initialized.
	 */
	Init init();

	/**
	 * The configuration to apply when the MOV started.
	 *
	 * @see OnStart
	 */
	interface Init {

		/**
		 * Define how the components must updated when the MOV is starting up.
		 *
		 * @return the mode to update the components defined in the database.
		 */
		@WithDefault("FINISH")
		public ComponentStartupMode components();

		/**
		 * Define how the connections must updated when the MOV is starting up.
		 *
		 * @return the mode to update the connections defined in the database.
		 */
		@WithDefault("DELETE")
		public ConnectionStartupMode connections();

		/**
		 * Define the mode that the MOV must follow when update the components library
		 * on start up.
		 *
		 * @return {@code true} if has to update the information of the defined
		 *         components at start up.
		 */
		@WithDefault("IF_STALE")
		public UpdateMode updateComponentsLibrary();

		/**
		 * The identifier of the topology to use when the MOV has to manage.
		 *
		 * @return the identifier of the topology to follow.
		 */
		@WithConverter(ObjectIdConverter.class)
		public Optional<ObjectId> topologyId();

		/**
		 * The path to the file that contains the topology that the MOV has to manage.
		 *
		 * @return the path to the file that contains the topology.
		 */
		public Optional<String> topologyPath();

	}

	/**
	 * Enumerates the possible modes for maintaining components on startup.
	 */
	public enum ComponentStartupMode {

		/**
		 * Maintain the components as they are in the database.
		 */
		PRESERVE,

		/**
		 * Mark as finished all the components.
		 */
		FINISH,

		/**
		 * Remove the components from the database.
		 */
		DROP;
	}

	/**
	 * Enumerates the possible modes for maintaining connections on startup.
	 */
	public enum ConnectionStartupMode {

		/**
		 * Maintain the connections as they are in the database.
		 */
		PRESERVE,

		/**
		 * Mark as disabled all the connections.
		 */
		DISABLE,

		/**
		 * Mark as deleted all the connections.
		 */
		DELETE,

		/**
		 * Remove the connections from the database.
		 */
		DROP;
	}

	/**
	 * The possible modes to update an element.
	 *
	 *
	 * @author VALAWAI
	 */
	public enum UpdateMode {

		/**
		 * Do not update at all.
		 */
		NEVER,

		/**
		 * Update all.
		 */
		ALWAYS,

		/**
		 * Update only if the last update plus a {@code UpdatePeriod} is less than the
		 * current time.
		 */
		IF_STALE
	}

	/**
	 * The URL where the MOV UI will be deployed.
	 *
	 * @return {@code true} if has to clean up on start up.
	 */
	@WithDefault("http://${quarkus.http.host}:${quarkus.http.port}")
	public String url();

	/**
	 * The configurations used to manage the library of components.
	 *
	 * @return the configuration of the components library.
	 */
	ComponentsLibrary componentsLibrary();

	/**
	 * The configuration of the components library.
	 */
	interface ComponentsLibrary {

		/**
		 * The epoch time, in seconds, of the last time that the component library was
		 * updated.
		 *
		 * @return the last update time stamp.
		 */
		@WithDefault("0")
		long lastUpdate();

		/**
		 * The time, in seconds, that has to pass before to update the component
		 * library.
		 *
		 * @return the seconds to wait until update the components library.
		 */
		@WithDefault("86400")
		long updatePeriod();
	}

	/**
	 * The property with epoch time, in seconds, of the last time the components
	 * library has been updated.
	 */
	public static final String COMPONENTS_LIBRARY_LAST_UPDATE_NAME = "mov.components-library.last-update";

	/**
	 * The configurations used to determine the behaviour of the MOV when receive an
	 * event.
	 *
	 * @return the configuration of the events management.
	 */
	public Events events();

	/**
	 * Determine the behaviour of the MOV when receive some specific events.
	 */
	interface Events {

		/**
		 * Specify what the MOV has to do after a component has been registered.
		 *
		 * @return the behaviour to do after the component has been registered.
		 */
		@WithDefault("AUTO_DISCOVER_CONNECTIONS")
		RegistrationBehavior registerComponent();

		/**
		 * The time, in seconds, that has to pass before to update the component
		 * library.
		 *
		 * @return the seconds to wait until update the components library.
		 */
		@WithDefault("DELETE_RELATED_CONNECTIONS")
		UnregistrationBehavior unregisterComponent();
	}

	/**
	 * Enumerates the possible behaviors that can be done when a component has been
	 * registered.
	 */
	public enum RegistrationBehavior {
		/**
		 * Do nothing after registering the component.
		 */
		DO_NOTHING,

		/**
		 * Automatically discover the possible connections for the component.
		 */
		AUTO_DISCOVER_CONNECTIONS,

		/**
		 * Check if the component can be applied to the current topology.
		 */
		CHECK_APPLICABILITY_TO_TOPOLOGY,

		/**
		 * Try to check if the component follows the topology, and if not, try to auto
		 * discover the connections.
		 */
		CHECK_TOPOLOGY_AND_AUTO_DISCOVER_IF_NEEDED
	}

	/**
	 * Enumerates the possible behaviors that can be done when a component is
	 * unregistered.
	 */
	public enum UnregistrationBehavior {
		/**
		 * Do nothing after unregistering the component.
		 */
		DO_NOTHING,

		/**
		 * Delete all the related connections for the component.
		 */
		DELETE_RELATED_CONNECTIONS
	}
}
