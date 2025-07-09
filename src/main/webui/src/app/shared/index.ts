/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

export { ConfigService } from './config.service';


/**
 * Convert a string to a pattern for search.
 */
export function toPattern(value: string[] | string | null | undefined): string | null {

	if (value != null) {

		if (Array.isArray(value)) {

			if (value.length > 0) {

				var pattern = value[0];
				if (value.length > 1) {

					pattern = "/" + pattern;
					for (var i = 1; i < value.length; i++) {

						pattern += "|" + value[i];
					}

					pattern += "/i";
				}

				return pattern;
			}

		} else {

			var pattern = "/";
			if (value != null) {

				var normalized = value.trim().replaceAll(/\W+/g, '.*') + ".*";
				normalized = normalized.replaceAll(/[\\.\\*]+/g, '.*').trim();
				pattern += normalized;

			} else {

				pattern += ".*";
			}
			pattern += "/i";
			return pattern;

		}
	}

	return null;

} 