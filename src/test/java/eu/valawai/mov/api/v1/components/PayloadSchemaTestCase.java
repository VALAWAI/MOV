/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

package eu.valawai.mov.api.v1.components;

import static eu.valawai.mov.ValueGenerator.rnd;

import eu.valawai.mov.api.ModelTestCase;

/**
 * Generic test for the classes that extends the {@link PayloadSchema}.
 *
 * @see PayloadSchema
 *
 * @param <T> type of schema to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class PayloadSchemaTestCase<T extends PayloadSchema> extends ModelTestCase<T> {

	/**
	 * Generate a next payload schema.
	 *
	 * @return the next payload schema.
	 */
	public static PayloadSchema nextPayloadSchema() {

		var option = 0;
		if (rnd().nextInt() % 3 == 0) {
			// only do 33% of times otherwise it create too big types
			option = rnd().nextInt(0, 4);
		}
		return switch (option) {
		case 0 -> new BasicPayloadSchemaTest().nextModel();
		case 1 -> new EnumPayloadSchemaTest().nextModel();
		case 2 -> new ObjectPayloadSchemaTest().nextModel();
		default -> new ArrayPayloadSchemaTest().nextModel();
		};
	}
}
