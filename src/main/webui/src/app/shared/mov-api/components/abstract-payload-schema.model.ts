/*
  Copyright 2022 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/

import { PayloadType } from "./payload-schema.model";

/**
 * The description of a payload of a message send or received from
 * {@link ChannelSchema}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
export abstract class AbstractPayloadSchema {

	/**
	 * The type of payload.
	 */
	public type: PayloadType | null = null;


}
