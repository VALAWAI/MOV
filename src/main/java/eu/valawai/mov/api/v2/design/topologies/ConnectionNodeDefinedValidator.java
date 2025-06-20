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
 * Validator class for the {@link ConnectionNodeDefined} constraint.
 *
 * @see ConnectionNodeDefined
 *
 * @author VALAWAI
 */
public class ConnectionNodeDefinedValidator implements ConstraintValidator<ConnectionNodeDefined, Topology> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(final Topology value, final ConstraintValidatorContext context) {

		if (value != null && value.connections != null) {

			if (value.nodes == null) {

				return false;
			}
			final var defined = new HashSet<String>();
			for (final var node : value.nodes) {

				defined.add(node.tag);
			}

			for (final var connection : value.connections) {

				if (!defined.contains(connection.source.nodeTag) || !defined.contains(connection.target.nodeTag)) {

					return false;
				}
			}
		}

		return true;
	}

}
