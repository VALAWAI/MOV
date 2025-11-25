/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/



import { Component, inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { toPattern } from '@app/shared';
import { MessageComponent, MessagesService } from '@app/shared/messages';
import { MovApiService, ComponentDefinitionPage, ComponentDefinition, ComponentType } from '@app/shared/mov-api';
import { MainService } from '@app/main';

/**
 * This is used to search for a component defined in the library of components.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-search-library',
	imports: [
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatIconModule,
		MatTableModule,
		MatPaginatorModule,
		MatMenuModule,
		MessageComponent,
		RouterModule,
		MatButtonModule,
		MatSelectModule
	],
	templateUrl: './search.component.html'
})
export class SearchLibraryComponent implements OnInit {

	/**
	 *  The service to mage the main header.
	 */
	private header = inject(MainService);

	/**
	 *  The service to interact with the MOV.
	 */
	private api = inject(MovApiService);

	/**
	 * The index of the current page.
	 */
	public pageIndex: number = 0;

	/**
	 * The size of teh page.
	 */
	public pageSize: number = 5;

	/**
	 * The page with the dfound componenets.
	 */
	public page: ComponentDefinitionPage | null = null;

	/**
	 * The form to define the search parameters.
	 */
	public searchForm = new FormGroup(
		{
			types: new FormControl<ComponentType[]>([]),
			pattern: new FormControl<string | null>(null)
		}
	);

	/**
	 *  The service to show user messages.
	 */
	private messages = inject(MessagesService);

	/**
	 * The columns to show.
	 */
	public displayedColumns: string[] = ["type", "name", "description", "actions"];

	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_search_code_page-title:Components library`);

		this.updatePage();

	}

	/**
	 * Update the page.
	 */
	public updatePage() {

		var pattern = toPattern(this.searchForm.controls.pattern.value);
		var typePattern = toPattern(this.searchForm.controls.types.value);
		var order = "+name,+desciption";
		var offset = this.pageIndex * this.pageSize;
		this.api.getComponentDefinitionPage(pattern, typePattern, order, offset, this.pageSize).subscribe(
			{
				next: page => this.page = page,
				error: err => this.messages.showMOVConnectionError(err)
			}
		);

	}

	/**
	 * Called whne the page parameters has changed.
	 */
	public pageChanged(event: PageEvent) {

		this.pageIndex = event.pageIndex;
		this.pageSize = event.pageSize;
		this.updatePage();

	}

}
