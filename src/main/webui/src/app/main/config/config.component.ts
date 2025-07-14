/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ConfigService } from '@app/shared';
import { MainService } from 'src/app/main';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { EFConnectionType } from '@foblex/flow';
import { MatSelectModule } from '@angular/material/select';

/**
 * Thei component allow to edit the configurtion of the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-config',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSlideToggleModule,
		RouterModule,
		MatSelectModule
	],
	templateUrl: './config.component.html'
})
export class ConfigComponent implements OnInit, OnDestroy {

	/**
	 *  Create the component.
	 */
	private header = inject(MainService);

	/**
	 * The configuration of teh APP.
	 */
	private conf = inject(ConfigService);

	/**
	 * The component to create forms.
	 */
	private fb = inject(FormBuilder);

	/**
	 * The subscription to the changes.
	 */
	private subscriptions: Subscription[] = [];

	/**
	 * The form to edit the	configuration.
	 */
	public confForm = this.fb.group(
		{
			pollingTime: this.fb.control<number>(this.conf.pollingTime, Validators.min(1000)),
			pollingIterations: this.fb.control<number>(this.conf.pollingIterations, Validators.min(10)),
			editorShowGrid: this.fb.control<boolean>(this.conf.editorShowGrid),
			editorAutoloadLastTopology: this.fb.control<boolean>(this.conf.editorAutoloadLastTopology),
			editorLastStoredTopologyId: this.fb.control<string | null>(this.conf.editorLastStoredTopologyId, Validators.pattern(/[0-9a-fA-F]{24}}/)),
			liveShowGrid: this.fb.control<boolean>(this.conf.liveShowGrid),
			liveEdgeType: this.fb.control<EFConnectionType>(this.conf.liveEdgeType),
			liveEdgeColor: this.fb.control<string>(this.conf.liveEdgeColor),
		}
	);

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_config_code_page-title:Settings`);

		this.subscribetoChangesTo(this.confForm.controls.pollingTime, (value) => { this.conf.pollingTime = value; });
		this.subscribetoChangesTo(this.confForm.controls.pollingIterations, (value) => { this.conf.pollingIterations = value; });
		this.subscribetoChangesTo(this.confForm.controls.editorShowGrid, (value) => { this.conf.editorShowGrid = value; });
		this.subscribetoChangesTo(this.confForm.controls.editorAutoloadLastTopology, (value) => { this.conf.editorAutoloadLastTopology = value; });
		this.subscribetoChangesTo(this.confForm.controls.liveShowGrid, (value) => { this.conf.liveShowGrid = value; });
		this.subscribetoChangesTo(this.confForm.controls.editorLastStoredTopologyId, (value) => { this.conf.editorLastStoredTopologyId = value; });
		this.subscribetoChangesTo(this.confForm.controls.liveEdgeType, (value) => { this.conf.liveEdgeType = value; });
		this.subscribetoChangesTo(this.confForm.controls.liveEdgeColor, (value) => { this.conf.liveEdgeColor = value; });
	}

	/**
	 * Unsubscribe to the changes.
	 */
	public ngOnDestroy() {

		for (var sub of this.subscriptions) {

			sub.unsubscribe();
		}
		this.subscriptions.splice(0, this.subscriptions.length);

	}

	/**
	 * Manage the chnages of the control.
	 */
	private subscribetoChangesTo<T>(control: FormControl<T | null>, setter: (value: T) => void) {

		this.subscriptions.push(
			control.valueChanges.subscribe(
				{
					next:
						() => {
							if (control.valid && control.value) {

								setter(control.value);
							}
						}
				}
			)
		);

	}

}
