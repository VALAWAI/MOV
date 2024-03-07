/*
  Copyright 2022-2026 VALAWAY

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { ArrayPayloadSchema } from "./array-payload-schema.model";
import { BasicPayloadSchema } from "./basic-payload-schema.model";
import { EnumPayloadSchema } from "./enum-payload-schema.model";
import { ObjectPayloadSchema } from "./object-payload-schema.model";

/**
 * The description of a payload of a message send or received from
 * {@link ChannelSchema}.
 *
 * @author VALAWAI
 */
export type PayloadSchema = BasicPayloadSchema
	| EnumPayloadSchema
	| ObjectPayloadSchema
	| ArrayPayloadSchema
	;


/**
 * The possible types of payload schema.
 *
 * @author VALAWAI
 */
export type PayloadType = 'BASIC' | 'ENUM' | 'OBJECT' | 'ARRAY';