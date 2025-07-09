/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from '@angular/core';
import { ComponentDefinition } from '@app/shared/mov-api';
import { MainService } from 'src/app/main';
import { AbstractComponentDefinitionComponent } from '../abstract-component-definition.component';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

/**
 * This is used to edit a defined component.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-component-edit',
	imports: [
		CommonModule,
		ReactiveFormsModule
	],
	templateUrl: './edit.component.html'
})
export class EditComponent extends AbstractComponentDefinitionComponent {

	/**
	 *  Create the component.
	 */
	private header = inject(MainService);

	/**
	 * The component to use in the component.
	 */
	public componentFomr = new FormGroup(
		{
			name: new FormControl<string | null>(null, Validators.required),
			description: new FormControl<string | null>(null)
		}
	);

	/**
	 * Update the component to edit.
	 */
	protected override updateComponent(component: ComponentDefinition): void {

		this.componentFomr.patchValue(
			{
				name: component.name,
				description: component.description
			},
			{
				emitEvent: false
			}
		);
	}


	/**
	 * Initialize the component.
	 */
	public override ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title to edit a component definition@@main_topology_design_components_edit_code_page-title:Component definition`);

		super.ngOnInit();
	}


}
