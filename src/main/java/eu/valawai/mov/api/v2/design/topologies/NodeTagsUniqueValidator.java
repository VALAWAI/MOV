/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.api.v2.design.topologies;

import java.util.HashSet;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class for the {@link NodeTagsUnique} constraint.
 *
 * @see NodeTagsUnique
 *
 * @author VALAWAI
 */
public class NodeTagsUniqueValidator implements ConstraintValidator<NodeTagsUnique, Topology> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(final Topology value, final ConstraintValidatorContext context) {

		if (value != null && value.nodes != null) {

			final var defined = new HashSet<String>();
			for (final var node : value.nodes) {

				if (!defined.add(node.tag)) {

					return false;
				}
			}
		}

		return true;
	}

}
