/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

import { GraphModule } from '@app/shared/graph';
import { ChannelSchema } from '@app/shared/mov-api';



@Component({
	standalone: true,
	selector: 'app-channel-schema-view',
	imports: [
    GraphModule,
    MatIconModule
],
	templateUrl: './channel-view.component.html'
})
export class ChannelSchemaViewComponent {

	/**
	 * The channel to display.
	 */
	@Input()
	public channel: ChannelSchema | null = null

}
