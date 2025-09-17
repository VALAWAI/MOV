/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { MessageComponent, MessagesService } from '@app/shared/messages';
import { MinTopology, MinTopologyPage, MovApiService } from '@app/shared/mov-api';
import { MainService } from 'src/app/main';
import { ConfirmRemoveTopologyDialog } from './confirm-remove-topology.dialog';
import { toPattern } from '@app/shared';
import { ApplyTopologyModule, ApplyTopologyService } from '@app/shared/apply-topology';

/**
 * Thei component allow to edit the seakerurtion of the MOV.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-seaker',
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatIconModule,
		MatButtonModule,
		MatMenuModule,
		MatTableModule,
		MatPaginatorModule,
		MessageComponent,
		RouterModule,
		MatDialogModule,
		ApplyTopologyModule
	],
	templateUrl: './seaker.component.html'
})
export class TopologySeakerComponent implements OnInit {

	/**
	 * The service to chnage the page header.
	 */
	private header = inject(MainService);

	/**
	 * The form to define the search query. 
	 */
	public searchForm = new FormGroup(
		{
			pattern: new FormControl<string | null>(null)
		}
	);

	/**
	 * The found topologies.
	 */
	public page: MinTopologyPage | null = null;

	/**
	 * The columns to display.
	 */
	public displayedColumns: string[] = ["name", "description", "actions"];

	/**
	 * The service to interact with the MOV.
	 */
	private api = inject(MovApiService);

	/**
	 * The service to provide messages.
	 */
	private messages = inject(MessagesService);

	/**
	 * The size for the pages.
	 */
	public pageSize: number = 15;

	/**
	 * The size for the pages.
	 */
	public pageIndex: number = 0;

	/**
	 * The service to manage the dialogs.
	 */
	private dialog = inject(MatDialog);

	/**
	 * The service to manage the topology application.
	 */
	private applyTopologyService = inject(ApplyTopologyService);


	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_seaker_code_page-title:Defined topologies`);
		this.updatePage();
	}

	/**
	 * Called when the page is changed.
	 */
	public pageChanged(event: PageEvent) {

		this.pageIndex = event.pageIndex;
		this.pageSize = event.pageSize;
		this.updatePage();
	}


	/**
	 * Called wjhen has to update the search.
	 */
	public updatePage() {

		var pattern = toPattern(this.searchForm.controls.pattern.value);
		var order = "+name,+description";
		var offset = this.pageIndex * this.pageSize;
		this.api.getMinTopologyPage(pattern, order, offset, this.pageSize).subscribe({
			next: page => this.page = page,
			error: err => this.messages.showMOVConnectionError(err)
		})

	}

	/**
	 * Called when has to delete a topology.
	 */
	public remove(topology: MinTopology) {

		this.dialog.open(ConfirmRemoveTopologyDialog, { data: topology }).afterClosed().subscribe(
			{
				next: result => {

					if (result) {

						this.api.deleteTopology(topology.id!).subscribe(
							{
								next: () => {

									this.messages.showSuccess(
										$localize`:The success message when a topology is removed@@main_topology_design_seaker_code_success-remove-msg:The topology has been removed.`
									);
									if (this.page && this.pageIndex > 0 && this.pageIndex * this.pageSize == this.page.total - 1) {

										this.pageIndex--;
									}
									this.updatePage();
								},
								error: err => this.messages.showMOVConnectionError(err)
							}
						);

					}
				}
			}
		);

	}

	/**
	 * Called when has to delete a topology.
	 */
	public apply(topology: MinTopology) {

		this.applyTopologyService.confirmAndApplyTopology(topology);
	}
}
