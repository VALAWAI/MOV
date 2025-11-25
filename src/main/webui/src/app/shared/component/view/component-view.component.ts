/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, Input } from '@angular/core';
import { ChannelsViewComponent } from '@app/shared/channels-view';
import { LoadingComponent } from '@app/shared/loading';
import { TimestampPipe } from '@app/shared/timestamp';
import { Component as MOVComponent } from 'src/app/shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-component-view',
	imports: [
    LoadingComponent,
    TimestampPipe,
    ChannelsViewComponent
],
	templateUrl: './component-view.component.html'
})
export class ComponentViewComponent {

	/**
	 * The component to view.
	 */
	@Input()
	public component: MOVComponent | null = null;

}
