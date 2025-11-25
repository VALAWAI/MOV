/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from '@angular/core';
import { ChannelSchema, ComponentDefinition, ComponentType, VersionInfo } from '@app/shared/mov-api';
import { MainService } from '@app/main';
import { AbstractComponentDefinitionComponent } from '../abstract-component-definition.component';

import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { VersionInfoEditorComponent } from '@app/shared/version-info';

/**
 * Check that the value is a vlaid JSON.
 */
export function jsonValidator(control: AbstractControl): ValidationErrors | null {

	if (control.value) {

		var value = control.value.trim();
		if (value.length > 0) {

			try {

				var parsed = JSON.parse(control.value);
				if (!Array.isArray(parsed)) {

					return { json: true };
				}

			} catch (ignored) {

				return { json: true };
			}
		}
	}

	return null;
};

/**
 * This is used to edit a defined component.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-component-edit',
	imports: [
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatIconModule,
		MatButtonModule,
		MatSelectModule,
		VersionInfoEditorComponent
	],
	templateUrl: './edit.component.html'
})
export class EditComponent extends AbstractComponentDefinitionComponent {

	/**
	 *  Create the component.
	 */
	private header = inject(MainService);

	/**
	 * The identifier of the component definition.
	 */
	private componentId: string | null = null;

	/**
	 * The component to use in the component.
	 */
	public componentForm = new FormGroup(
		{
			type: new FormControl<ComponentType | null>(null, Validators.required),
			name: new FormControl<string | null>(null, Validators.required),
			description: new FormControl<string | null>(null),
			docsLink: new FormControl<string | null>(null, Validators.pattern(/^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/)),
			gitHubLink: new FormControl<string | null>(null, Validators.pattern(/^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/)),
			version: new FormControl<VersionInfo | null>(null),
			apiVersion: new FormControl<VersionInfo | null>(null),
			channels: new FormControl<string | null>(null, jsonValidator)
		}
	);

	/**
	 * Update the component to edit.
	 */
	protected override updateComponent(component: ComponentDefinition): void {

		this.componentForm.patchValue(
			{
				type: component.type,
				name: component.name,
				description: component.description,
				docsLink: component.docsLink,
				gitHubLink: component.gitHubLink,
				version: component.version,
				apiVersion: component.apiVersion,
				channels: component.channels != null ? JSON.stringify(component.channels, null, 2) : null
			},
			{
				emitEvent: false
			}
		);
		this.componentId = component.id;
	}


	/**
	 * Initialize the component.
	 */
	public override ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title to edit a component definition@@main_topology_design_components_edit_code_page-title:Edit component definition`);

		super.ngOnInit();
	}

	/**
	 * Called when has to update the component.
	 */
	public doUpdate() {

		if (this.componentForm.valid) {

			var component = new ComponentDefinition();
			component.id = this.componentId;
			component.type = this.componentForm.controls.type.value;
			component.name = this.componentForm.controls.name.value;
			component.description = this.componentForm.controls.description.value;
			component.docsLink = this.componentForm.controls.docsLink.value;
			component.gitHubLink = this.componentForm.controls.gitHubLink.value;
			component.version = this.componentForm.controls.version.value;
			component.apiVersion = this.componentForm.controls.apiVersion.value;
			if (this.componentForm.controls.channels.value) {

				var value = this.componentForm.controls.channels.value.trim();
				if (value.length > 0) {

					component.channels = JSON.parse(value) as ChannelSchema[];
				}
			}
			this.api.updateComponentDefinition(this.componentId!, component).subscribe(
				{
					next: () => {

						this.messages.showSuccess($localize`:The success mesage whne the componet has bene updated@@main_topology_design_components_edit_code_update-success-msg:Component definition updated!`);
						this.router.navigate(['/main/topology/design/components', this.componentId, 'view']);
					},
					error: err => this.messages.showMOVConnectionError(err)
				}
			);

		} else {

			this.componentForm.markAllAsTouched();
		}

	}

}
