/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.persistence;

import eu.valawai.mov.events.ComponentType;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;

/**
 * Define a component that is defined on the MOV.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Entity(name = "component")
public class ComponentEntity extends PanacheEntity {

	/**
	 * The name of the component.
	 */
	public String name;

	/**
	 * The version of the components.
	 */
	public String version;

	/**
	 * The type of component.
	 */
	public ComponentType type;

}
