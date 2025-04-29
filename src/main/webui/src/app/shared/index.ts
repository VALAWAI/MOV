/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

export function pullingTime() {

	var pool = localStorage.getItem('POOLING_TIME');
	if (pool) {

		var time = Number(pool);
		if (!isNaN(time)) {

			return time;
		}
	}
	return 1500;
}

export function updatePullingTime(time: number) {

	localStorage.setItem('POOLING_TIME', String(time));
}