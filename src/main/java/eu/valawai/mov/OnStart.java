/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * The class that manage when the application has been started.
 *
 * @author VALAWAI
 */
@ApplicationScoped
public class OnStart {

	/**
	 * Called when the application has been started.
	 *
	 * @param event that contains the start status.
	 */
	public void handle(@Observes StartupEvent event) {

	}
}
