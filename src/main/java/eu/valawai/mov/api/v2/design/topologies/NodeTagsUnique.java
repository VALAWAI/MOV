/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom Hibernate validator for the topology to check that the nodes tags are
 * not duplicated. * Annotation to mark that the tag of the nodes has to be
 * unique.
 *
 * @see NodeTagsUniqueValidator
 *
 * @author VALAWAI
 */
@Constraint(validatedBy = NodeTagsUniqueValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface NodeTagsUnique {

	/**
	 * Return the error message to show if it is not valid.
	 *
	 * @return the error message.
	 */
	String message() default "Node tags must be unique.";

	/**
	 * The groups that this constraint belongs to.
	 *
	 * @return the groups associated to the validator.
	 */
	Class<?>[] groups() default {};

	/**
	 * The payload for this constraint.
	 *
	 * @return the payloads associated to the validator.
	 */
	Class<? extends Payload>[] payload() default {};

}
