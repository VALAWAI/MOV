/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule, } from '@angular/common';
import { Component, inject, Input, OnDestroy } from '@angular/core';
import { VersionInfo } from '../mov-api';
import { AbstractControl, ControlValueAccessor, FormBuilder, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule, TouchedChangeEvent, Validator, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Subscription } from 'rxjs';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';

@Component({
	standalone: true,
	selector: 'app-version-info-editor',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatDatepickerModule
	],
	templateUrl: './version-info-editor.component.html',
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			multi: true,
			useExisting: VersionInfoEditorComponent
		},
		{
			provide: NG_VALIDATORS,
			multi: true,
			useExisting: VersionInfoEditorComponent
		},
		provideMomentDateAdapter()
	]
})
export class VersionInfoEditorComponent implements ControlValueAccessor, Validator, OnDestroy {

	/**
	 * The component to create form components.
	 */
	private fb = inject(FormBuilder);


	/**
	 * The form to edit the version values.
	 */
	public versionForm = this.fb.group(
		{
			name: this.fb.control<string | null>(null, [Validators.required, Validators.pattern(/\d+\\.\d+\\.\d+/)]),
			since: this.fb.control<Date | null>({ value: null, disabled: true })
		}
	);


	/**
	 * Subscritions to call when change.
	 */
	private subscriptions: Subscription[] = [];

	/**
	 * Called to unsubscribe to the changes.
	 */
	public ngOnDestroy() {

		for (let sub of this.subscriptions) {

			sub.unsubscribe();
		}
		this.subscriptions = [];
	}

	/**
	 * Subscribe for a change in the editor.
	 */
	public registerOnChange(onChange: (value: VersionInfo | null) => void) {

		var sub = this.versionForm.valueChanges.subscribe({
			next: () => {

				var version: VersionInfo | null = null;
				if (this.versionForm.valid) {

					version = new VersionInfo();
					version.name = this.versionForm.controls.name.value;
					if (this.versionForm.controls.since.value) {

						version.since = this.versionForm.controls.since.value.getTime() / 1000;

					}
				}
				onChange(version);
			}
		})
		this.subscriptions.push(sub);
	}

	/**
	 * Register to notify when the component is toucheed.
	 */
	public registerOnTouched(onTouched: (value: unknown) => void) {


		var sub = this.versionForm.events.subscribe({
			next: event => {

				if (event instanceof TouchedChangeEvent) {

					onTouched(event);
				}
			}
		})
		this.subscriptions.push(sub);

	}

	/**
	 * Change the disable state.
	 */
	public setDisabledState(disabled: boolean) {

		if (disabled) {

			this.versionForm.disable();

		} else {

			this.versionForm.enable();
		}
	}

	/**
	 * Change the value of the form.
	 */
	public writeValue(value: VersionInfo | null | undefined) {

		if (value && typeof value === 'object') {

			var version = value as VersionInfo;
			var since: Date | null = null;
			if (version.since) {

				since = new Date(version.since * 1000);
			}
			this.versionForm.patchValue(
				{
					name: version.name,
					since: since
				},
				{
					emitEvent: false
				}
			);

		}
	}

	/**
	 * Validate the form.
	 */
	public validate(control: AbstractControl) {

		if (this.versionForm.valid) {
			return null;
		}

		return this.versionForm.errors;
	}
}
