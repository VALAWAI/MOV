/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Pipe, PipeTransform } from '@angular/core';
import { MinComponent} from 'src/app/shared/mov-api';

/*
 * Beautify the name of a component.
 * Ex for component.name = valawai/c1_echo_message:
 *   component | componentNameBeautifier
 * Generates: 
 *   Echo message
*/
@Pipe({
	standalone: false,
	name: 'componentNameBeautifier'
})
export class ComponentNameBeautifier implements PipeTransform {

	/**
	 * Convert the time stmap to a string.
	 */
	transform(value: MinComponent | null | undefined): string  {

		if (value != null && value.name != null) {

			var name = value.name.trim();
			if( name.startsWith("valawai/") ){
				
				name = name.substring(8);
			}
			
			if( name.match(/^[c|C][0|1|2]_.+/) ){

				name = name.substring(3);
			}

			name = name.replaceAll("_"," ");
			name = name.charAt(0).toUpperCase()+ name.substring(1);
			
			return name;

		}else{
			
			return '';
		}

	}
}