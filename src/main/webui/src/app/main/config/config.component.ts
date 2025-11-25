/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ConfigService } from '@app/shared';
import { MainService } from '@app/main';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { EFConnectionType } from '@foblex/flow';
import { MatSelectModule } from '@angular/material/select';
import {
	LiveConfiguration,
	MovApiService,
	TOPOLOGY_BEHAVIOR_NAMES,
	TopologyBehavior,
	TopologyBehaviourToNamePipe
} from '@app/shared/mov-api';
import { MessagesService } from '@app/shared/messages';
import { ApplyTopologyService } from '@app/shared/apply-topology';

/**
 * Thei component allow to edit the configurtion of the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-config',
	imports: [
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSlideToggleModule,
		RouterModule,
		MatSelectModule,
		TopologyBehaviourToNamePipe
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
	 * Form builder service.
	 */
	private readonly fb = inject(FormBuilder);

	/**
	 * The subscription to the changes.
	 */
	private subscriptions: Subscription[] = [];

	/**
	 * The current live configuration.
	  */
	private liveConfiguration: LiveConfiguration = new LiveConfiguration();

	/**
	 * The service to show messages.
	 */
	private readonly messages = inject(MessagesService);

	/**
	 * Service to access to the MOV API.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The service to apply a topology.
	 */
	public readonly applyTopologyService = inject(ApplyTopologyService);

	/**
	 * The form to edit the	configuration.
	 */
	public confForm = this.fb.group(
		{
			pollingTime: this.fb.control<number>(this.conf.pollingTime, [Validators.required, Validators.min(1000)]),
			pollingIterations: this.fb.control<number>(this.conf.pollingIterations, [Validators.required, Validators.min(10)]),
			editorShowGrid: this.fb.control<boolean>(this.conf.editorShowGrid),
			editorAutoloadLastTopology: this.fb.control<boolean>(this.conf.editorAutoloadLastTopology),
			editorLastStoredTopologyId: this.fb.control<string | null>(this.conf.editorLastStoredTopologyId, Validators.pattern(/[0-9a-fA-F]{24}/)),
			liveShowGrid: this.fb.control<boolean>(this.conf.liveShowGrid),
			liveEdgeType: this.fb.control<EFConnectionType>(this.conf.liveEdgeType),
			liveMaxNodes: this.fb.control<number>(this.conf.liveMaxNodes, [Validators.required, Validators.min(100)]),
			editorAutosaveTime: this.fb.control<number>(this.conf.editorAutosaveTime, [Validators.required, Validators.min(1000)]),
			editorMaxHistory: this.fb.control<number>(this.conf.editorMaxHistory, [Validators.required, Validators.min(0)]),
			liveTopologyId: this.fb.control<string | null>(null, Validators.pattern(/[0-9a-fA-F]{24}/)),
			liveRegisterComponentBehaviour: this.fb.control<TopologyBehavior>("AUTO_DISCOVER", Validators.required),
			liveCreateConnectionBehaviour: this.fb.control<TopologyBehavior>("AUTO_DISCOVER", Validators.required),
		}
	);

	/**
	 * The names for the topology behaviours.
	 */
	public readonly topologyBehaviorNames = TOPOLOGY_BEHAVIOR_NAMES;

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_config_code_page-title:Settings`);

		this.liveConfiguration.topologyId = null;
		this.liveConfiguration.registerComponent = "AUTO_DISCOVER";
		this.liveConfiguration.createConnection = "AUTO_DISCOVER";

		this.api.getLiveConfiguration().subscribe(
			{
				next: (conf) => this.updateLiveConfiguration(conf)
			}
		);

		this.subscribetoChangesTo(this.confForm.controls.pollingTime, (value) => { this.conf.pollingTime = value!; });
		this.subscribetoChangesTo(this.confForm.controls.pollingIterations, (value) => { this.conf.pollingIterations = value!; });
		this.subscribetoChangesTo(this.confForm.controls.editorShowGrid, (value) => { this.conf.editorShowGrid = value!; });
		this.subscribetoChangesTo(this.confForm.controls.editorAutoloadLastTopology, (value) => { this.conf.editorAutoloadLastTopology = value!; });
		this.subscribetoChangesTo(this.confForm.controls.liveShowGrid, (value) => { this.conf.liveShowGrid = value!; });
		this.subscribetoChangesTo(this.confForm.controls.editorLastStoredTopologyId, (value) => { this.conf.editorLastStoredTopologyId = value; });
		this.subscribetoChangesTo(this.confForm.controls.liveEdgeType, (value) => { this.conf.liveEdgeType = value!; });
		this.subscribetoChangesTo(this.confForm.controls.liveMaxNodes, (value) => { this.conf.liveMaxNodes = value!; });
		this.subscribetoChangesTo(this.confForm.controls.editorAutosaveTime, (value) => { this.conf.editorAutosaveTime = value!; });
		this.subscribetoChangesTo(this.confForm.controls.editorMaxHistory, (value) => { this.conf.editorMaxHistory = value!; });
		this.subscribetoChangesTo(
			this.confForm.controls.liveTopologyId,
			(value) => {

				if (value == null || value.length == 0) {

					if (this.liveConfiguration.topologyId != null) {
						// Remove the topology.
						this.liveConfiguration.topologyId = null;
						this.storeLiveConfiguration();
					}

				} else {

					this.api.getTopology(value).subscribe(
						{
							next: topology => {

								this.applyTopologyService.confirmAndApplyTopology(topology, updated => this.updateLiveConfiguration(updated));
							},
							error: err => {

								this.messages.showError($localize`:Error message shown when the user set an undefined topology id in the configuration@@main_config_code_undefined-topology-id:Undefined topology id`);
								this.confForm.controls.liveTopologyId.setErrors({ notFound: true });
								console.error(err);
							}
						}
					);
				}
			}
		);
		this.subscribetoChangesTo(
			this.confForm.controls.liveRegisterComponentBehaviour,
			(value) => {

				this.liveConfiguration.registerComponent = value;
				this.storeLiveConfiguration();
			}
		);
		this.subscribetoChangesTo(
			this.confForm.controls.liveCreateConnectionBehaviour,
			(value) => {

				this.liveConfiguration.createConnection = value;
				this.storeLiveConfiguration();
			}
		);
	}

	/**
	 * Called when want to update the live configuration.
	 */
	private updateLiveConfiguration(conf: LiveConfiguration) {

		this.liveConfiguration = conf;
		this.confForm.patchValue(
			{
				liveTopologyId: conf.topologyId,
				liveRegisterComponentBehaviour: conf.registerComponent,
				liveCreateConnectionBehaviour: conf.createConnection
			},
			{
				emitEvent: false
			}
		);

	}

	/**
	 * Called when has to store the live configuration.
	 */
	private storeLiveConfiguration() {

		this.api.setLiveConfiguration(this.liveConfiguration).subscribe(
			{
				next: () => {

				},
				error: (err) => this.messages.showMOVConnectionError(err)
			}
		);

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
	private subscribetoChangesTo<T>(control: FormControl<T | null>, setter: (value: T | null) => void) {

		this.subscriptions.push(
			control.valueChanges.subscribe(
				{
					next:
						() => {
							if (control.valid) {

								setter(control.value);
							}
						}
				}
			)
		);

	}

}
