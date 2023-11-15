/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A model that implements the basic operation by reflection.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ReflectionModel {

	/**
	 * Reflections equals.
	 *
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see EqualsBuilder#reflectionEquals(Object, Object,String...)
	 */
	@Override
	public boolean equals(final Object obj) {

		return EqualsBuilder.reflectionEquals(this, obj);

	}

	/**
	 * Reflection hash code.
	 *
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 * @see HashCodeBuilder#reflectionHashCode(Object, String...)
	 */
	@Override
	public int hashCode() {

		return HashCodeBuilder.reflectionHashCode(this);

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#toString()
	 * @see ToStringBuilder#reflectionToString(Object)
	 */
	@Override
	public String toString() {

		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);

	}

}
