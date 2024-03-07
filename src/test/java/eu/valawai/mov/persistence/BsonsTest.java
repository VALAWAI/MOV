/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link Bsons}.
 *
 * @see Bsons
 *
 * @author VALAWAI
 */
public class BsonsTest {

	/**
	 * Check convert to a string a Bson.
	 */
	@Test
	public void shouldToStringNUllBson() {

		assertEquals("null", Bsons.toString((Bson) null));

	}

	/**
	 * Check convert to a string a Bson array.
	 */
	@Test
	public void shouldToStringNUllBsonArray() {

		assertEquals("null", Bsons.toString((Bson[]) null));

	}

	/**
	 * Check convert to a string a Bson iterable.
	 */
	@Test
	public void shouldToStringNUllBsonIterable() {

		assertEquals("null", Bsons.toString((Iterable<Bson>) null));

	}

	/**
	 * Check convert to a string a Bson iterable.
	 */
	@Test
	public void shouldToStringIterable() {

		final var expected = "[null,{\"key1\": 1, \"key2\": false},{\"key3\": \"value\", \"key4\": 0.1, \"key5\": {\"key6\": \"child\"}}]";
		final var bsons = new ArrayList<Bson>();
		bsons.add(null);
		bsons.add(new Document("key1", 1).append("key2", false));
		bsons.add(new Document("key3", "value").append("key4", 0.1).append("key5", new Document("key6", "child")));
		assertEquals(expected, Bsons.toString(bsons));

	}

	/**
	 * Check convert to a string a Bson array.
	 */
	@Test
	public void shouldToStringArray() {

		final var expected = "[null,{\"key1\": 1, \"key2\": false},{\"key3\": \"value\", \"key4\": 0.1, \"key5\": {\"key6\": \"child\"}}]";
		final var result = Bsons.toString(null, new Document("key1", 1).append("key2", false),
				new Document("key3", "value").append("key4", 0.1).append("key5", new Document("key6", "child")));
		assertEquals(expected, result);

	}

}
