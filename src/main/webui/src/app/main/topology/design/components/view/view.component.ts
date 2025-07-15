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
import { VersionInfoViewComponent } from '@app/shared/version-info/';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { LoadingComponent } from '@app/shared/loading';
import { ChannelsViewComponent } from '@app/shared/channels-view';

/**
 * This is used to show the detail of a component in the library.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-component-view',
	imports: [
		CommonModule,
		VersionInfoViewComponent,
		MatButtonModule,
		MatIconModule,
		RouterModule,
		MatMenuModule,
		LoadingComponent,
		ChannelsViewComponent
	],
	templateUrl: './view.component.html'
})
export class ViewComponent extends AbstractComponentDefinitionComponent {

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

		this.header.changeHeaderTitle($localize`:The header title to view a component definition@@main_topology_design_components_view_code_page-title:Component definition`);

		super.ngOnInit();
	}


}
