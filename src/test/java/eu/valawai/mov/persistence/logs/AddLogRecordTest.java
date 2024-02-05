/*
  Copyright 2024 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.persistence.logs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.valawai.mov.MasterOfValawaiTestCase;
import eu.valawai.mov.TimeManager;
import eu.valawai.mov.api.v1.logs.LogRecordTest;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test the {@link AddLogRecord}.
 *
 * @see AddLogRecord
 *
 * @author VALAWAI
 */
@QuarkusTest
public class AddLogRecordTest extends MasterOfValawaiTestCase {

	/**
	 * Should add a record.
	 */
	@Test
	public void shouldAddRecord() {

		final var log = new LogRecordTest().nextModel();
		final var time = TimeManager.now();
		final var result = this.assertExecutionNotNull(AddLogRecord.fresh().withLog(log));
		assertTrue(result);

		final LogEntity stored = this
				.assertItemNotNull(LogEntity.find("message = ?1", Sort.descending("_id"), log.message).firstResult());
		assertEquals(log.payload, stored.payload);
		assertTrue(time <= stored.timestamp);

	}

}
