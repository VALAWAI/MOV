/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

export type HealthStatus = 'DOWN' | 'UP' | null;

/**
 * The information of health of the MOV.
 *
 * @author VALAWAI
 */
export class HealthInfo {

	/**
	 * The version of the MOV.
	 */
	public status: HealthStatus = null;

	/**
	 * The profile of the quarkus platform.
	 */
	public checks: HealthCheck[] | null = null;

}

/**
 * The information of a check.
 */
export class HealthCheck {

	/**
	 * The name of the check.
	 */
	public name: string | null = null;

	/**
	 * The version of the check.
	 */
	public status: HealthStatus = null;

	/**
	 * The data sssociated to the check.
	 */
	public data: [string: string] | null = null;

}