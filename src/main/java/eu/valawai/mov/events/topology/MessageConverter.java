/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.events.topology;

import eu.valawai.mov.persistence.live.topology.TopologyConnectionEntity;
import io.quarkus.logging.Log;
import io.roastedroot.quickjs4j.annotations.ScriptInterface;
import io.vertx.core.json.JsonObject;

/**
 * Define the methods that the Java script code must to provide for convert the
 * published source message to the subscribed target message in a
 * {@link TopologyConnectionEntity}.
 *
 * @author VALAWAI
 */
@ScriptInterface(context = MessageConverterContext.class, excluded = { "convertMessage", "equals", "toString", "wait",
		"getClass", "hashCode", "notify", "notifyAll", "clone", "finalize" })
public interface MessageConverter {

	/**
	 * The conversion of the encoded message.
	 *
	 * @param source message to convert.
	 *
	 * @return The encoded conversion result.
	 */
	public String convertEncodedMessage(String source);

	/**
	 * The conversion of the message.
	 *
	 * @param source message to convert.
	 *
	 * @return The conversion result.
	 */
	public default JsonObject convertMessage(JsonObject source) {

		try {

			final var encodedSource = source.encode();
			final var encodedTarget = this.convertEncodedMessage(encodedSource);
			return new JsonObject(encodedTarget);

		} catch (final Throwable error) {

			Log.errorv(error, "Cannot convert the message {0}", source);
			return null;
		}

	}

}
