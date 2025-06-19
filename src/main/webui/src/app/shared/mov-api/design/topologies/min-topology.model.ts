/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


/**
 *  Represents the minimal information required to define a topology between VALAWAI components
 *  for the execution of a value-aware application. 
 *
 * @author VALAWAI
 */
export class MinTopology {

	/**
	 * The unique identifier of the topology. This is system-generated and read-only.
	 */
	public id: string | null = null;

	/**
	 * A unique, human-readable name for the topology. This field is mandatory.
	 */
	public name: string | null = null;

	/**
	 * An optional, detailed description of the topology's purpose or design.
	 */
	public description: string | null = null;

}
