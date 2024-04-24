/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/


import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MainService } from 'src/app/main';
import { MessagesService } from 'src/app/shared/messages';
import { ComponentType, LogRecord, MovApiService } from 'src/app/shared/mov-api';
import { ComponentToRegister } from 'src/app/shared/mov-api/components/component-to-register.model';


@Component({
	selector: 'app-register-component',
	templateUrl: './register-component.component.html',
	styleUrls: ['./register-component.component.css']
})
export class RegisterComponentComponent implements OnInit {

	/**
	 * The form to define the component to register.
	 */
	public form = this.fb.group(
		{
			type: this.fb.control<ComponentType | null>(null, Validators.required),
			name: this.fb.control<string | null>(null, [Validators.required, Validators.pattern(/c[0|1|2]_\w+/)]),
			version: this.fb.control<string | null>(null, [Validators.required, Validators.pattern(/\d+\.\d+\.\d+/)]),
			asyncapiYaml: this.fb.control<string | null>(null, Validators.required)
		}
	);

	/**
	 * This is {@code true} if the component is registering.
	 */
	public registering: boolean = false;

	/**
	 * The timestamp of the last log.
	 */
	private lastLogTimestamp: number = 0;

	/**
	 *  Create the component.
	 */
	constructor(
		private header: MainService,
		private mov: MovApiService,
		private messages: MessagesService,
		private fb: FormBuilder,
		private router: Router
	) {

	}


	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the register component page@@main_components_register_code_page-title:Register a new component`);


	}

	/**
	 * Register the component.
	 */
	public register() {

		if (this.form.valid) {

			this.registering = true;
			this.mov.getLogRecordPage(null, null,null, null, "-timestamp", 0, 1).subscribe(
				{
					next: page => {

						if (page && page.logs != null && page.logs.length > 0) {

							this.lastLogTimestamp = page.logs[0].timestamp || 0;

						} else {

							this.lastLogTimestamp = 0;
						}
						this.registerComponent();

					},
					error: () => {

						this.lastLogTimestamp = 0;
						this.registerComponent();
					}
				}
			);


		} else {

			this.form.markAllAsTouched();
		}

	}

	/**
	 * Called when want to register a component.
	 */
	private registerComponent() {

		var component = this.form.value as ComponentToRegister;
		this.mov.registerComponent(component).subscribe({
			next: () => this.checkRegisteredComponent(component), error: err => this.notifyRegisterError(err)
		});
	}

	/**
	 * Notify that cannot register the component.
	 */
	private notifyRegisterError(err: any | null = null) {

		this.registering = false;
		this.messages.showError($localize`:The error message when canot register a component@@main_components_register_code_error-message:Cananot register the component`);
		if (err != null) {

			console.error(err);
		}

	}


	/**
	 * Check if the component has been registered.
	 */
	private checkRegisteredComponent(component: ComponentToRegister, times: number = 100) {

		this.mov.getLogRecordPage(null, null, null, null, "-timestamp", 0, 100).subscribe(
			{
				next: page => {

					if (page.logs != null) {

						for (var log of page.logs) {

							if (log.timestamp != null && log.timestamp > this.lastLogTimestamp && this.match(component, log)) {

								if (log.level == 'INFO') {

									this.mov.getMinComponentPage(component.name, component.type, null, null, "-since", 0, 100).subscribe(
										{
											next: page => {

												if (page.components != null && page.components.length > 0) {

													this.registering = false;
													this.messages.showSuccess($localize`:Success message when registered a new component@@main_components_register_code_success-message:Registered the new component`);
													this.router.navigate(['/main/components', page.components[0].id, 'show']);

												} else {

													this.notifyRegisterError();
												}

											}, error: err => this.notifyRegisterError(err)
										}
									);

								} else {

									this.notifyRegisterError();
								}
								return;
							}
						}
					}

					if (times > 0) {

						setTimeout(() => this.checkRegisteredComponent(component, times - 1), 1000);

					} else {

						this.notifyRegisterError();
					}

				}, error: err => this.notifyRegisterError(err)
			}
		);
	}

	/**
	 * Check if a component is refered in a log message.
	 */
	private match(component: ComponentToRegister, log: LogRecord): boolean {

		if (log.payload != null) {

			if (component.type != null) {

				var content = log.payload;
				var index = content.indexOf(component.type);
				if (index > 0 && component.name != null) {

					content = content.substring(index + 1);
					index = content.indexOf(component.name);
					if (index > 0 && component.version != null) {

						content = content.substring(index + 1);
						index = content.indexOf(component.version);
						if (index > 0) {

							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
