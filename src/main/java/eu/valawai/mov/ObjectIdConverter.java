/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * The call that convert an Sting to a {@link ObjectId}.
 *
 * @see ObjectId
 *
 * @author VALAWAI
 */
public class ObjectIdConverter implements Converter<ObjectId> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectId convert(String value) throws IllegalArgumentException, NullPointerException {

		return new ObjectId(value);
	}

}
