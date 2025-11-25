/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, Input } from '@angular/core';
import { ChannelSchema } from '../mov-api';

import { NgxJsonViewerModule } from 'ngx-json-viewer';

@Component({
	standalone: true,
	selector: 'app-channels-view',
	imports: [
    NgxJsonViewerModule
],
	templateUrl: './channels-view.component.html'
})
export class ChannelsViewComponent {

	/**
	 * The channles to view.
	 */
	private schemas: ChannelSchema[] = [];


	/**
	 * Set the channles to view.
	 */
	@Input()
	public set channels(channels: ChannelSchema[] | ChannelSchema | null | undefined) {

		this.schemas = [];
		if (channels != null) {

			if (Array.isArray(channels)) {

				this.schemas = channels;

			} else {

				this.schemas = [channels];
			}
		}

	}

	/**
	 * Get the channles to view.
	 */
	public get channels(): ChannelSchema[] {

		return this.schemas;

	}


}
