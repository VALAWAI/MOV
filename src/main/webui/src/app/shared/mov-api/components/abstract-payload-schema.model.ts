/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { PayloadType } from "./payload-schema.model";

/**
 * The description of a payload of a message send or received from
 * {@link ChannelSchema}.
 *
 * @author VALAWAI
 */
export abstract class AbstractPayloadSchema {

	/**
	 * The type of payload.
	 */
	public type: PayloadType | null = null;


}
