/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from '@angular/core';
import { ComponentDefinition, ComponentType, VersionInfo } from '@app/shared/mov-api';
import { MainService } from 'src/app/main';
import { AbstractComponentDefinitionComponent } from '../abstract-component-definition.component';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { VersionInfoEditorComponent } from '@app/shared/version-info';

/**
 * This is used to edit a defined component.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-component-edit',
	imports: [
		CommonModule,
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
			apiVersion: new FormControl<VersionInfo | null>(null)
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
				apiVersion: component.apiVersion
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
			this.api.updateComponentDefinition(this.componentId!, component).subscribe(
				{
					next: () => this.router.navigate(['/main/topology/design/components', this.componentId, 'view']),
					error: err => this.messages.showMOVConnectionError(err)
				}
			);

		} else {

			this.componentForm.markAllAsTouched();
		}

	}

}
