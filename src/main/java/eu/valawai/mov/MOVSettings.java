/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

/**
 * The names of the MOV settings.
 *
 * @author VALAWAI
 */
public interface MOVSettings {

	/**
	 * The property used to force MOV to clean the active components and connection
	 * on start up.
	 */
	public static final String CLEAN_ON_STARTUP = "mov.cleanOnStartup";

	/**
	 * The property used to force MOV to update the components library on start up.
	 */
	public static final String COMPONENTS_LIBRARY_UPDATE_ON_START = "mov.definitions.components.updateOnStart";

	/**
	 * The property with epoch time, in seconds, of the last time the components
	 * library has been updated.
	 */
	public static final String COMPONENTS_LIBRARY_LAST_UPDATE = "mov.definitions.components.lastUpdate";

	/**
	 * The property that indicated the time that has to pass before to update the
	 * components library.
	 */
	public static final String COMPONENTS_LIBRARY_UPDATE_PERIOD = "mov.definitions.components.updatePeriod";

}
