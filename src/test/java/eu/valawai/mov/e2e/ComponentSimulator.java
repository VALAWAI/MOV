/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.mov.e2e;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import eu.valawai.mov.events.MovEventTestCase.TestMQQueue;

/**
 * Simulate a component that is registered in the MOV.
 *
 * @author VALAWAI
 */
public class ComponentSimulator {

	/**
	 * The identifier of the component.
	 */
	public ObjectId id;

	/**
	 * The messages that has been received.
	 */
	public final Map<String, TestMQQueue> queues = Collections.synchronizedMap(new HashMap<>());

}
