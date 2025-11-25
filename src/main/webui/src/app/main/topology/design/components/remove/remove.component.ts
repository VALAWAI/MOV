/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject } from '@angular/core';
import { ComponentDefinition } from '@app/shared/mov-api';
import { MainService } from '@app/main';
import { AbstractComponentDefinitionComponent } from '../abstract-component-definition.component';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { LoadingComponent } from '@app/shared/loading';
import { RouterModule } from '@angular/router';

/**
 * This is used to show the detail of a component in the library.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-component-view',
	imports: [
		MatButtonModule,
		MatIconModule,
		LoadingComponent,
		RouterModule
	],
	templateUrl: './remove.component.html'
})
export class RemoveComponent extends AbstractComponentDefinitionComponent {

	/**
	 *  Create the component.
	 */
	private header = inject(MainService);

	/**
	 * The component to use in the component.
	 */
	public component: ComponentDefinition | null = null;

	/**
	 * Update the component to view.
	 */
	protected override updateComponent(component: ComponentDefinition): void {

		this.component = component;
	}


	/**
	 * Initialize the component.
	 */
	public override ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title to remove a component definition@@main_topology_design_components_remove_code_page-title:Remove component definition`);

		super.ngOnInit();
	}

	/**
	 * Remove the component.
	 */
	public removeComponent() {

		this.api.removeComponentDefinition(this.component!.id!).subscribe(
			{
				next: () => {

					this.messages.showSuccess($localize`:The remove component message success mesage@@main_topology_design_components_remove_code_success-msg:The component definition has been removed.`);
					this.router.navigate(['/main/topology/design/components/search']);
				},
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}


}
