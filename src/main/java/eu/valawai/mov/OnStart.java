/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
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
