/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, inject, OnInit } from '@angular/core';
import { ConfigService } from '@app/shared';
import { LoadingComponent } from '@app/shared/loading';
import { MessagesService } from '@app/shared/messages';
import { MovApiService } from '@app/shared/mov-api';
import { Observable } from 'rxjs';
import { MainService } from 'src/app/main';

/**
 * This is used to manage the posible update taht cna be used into the design of a topology.
 */
@Component({
	standalone: true,
	selector: 'app-topology-design-components-update-library',
	imports: [
		
	],
	templateUrl: './update.component.html'
})
export class UpdateLibraryComponent implements OnInit {

	/**
	 *  The service over the main view. 
	 */
	private readonly header = inject(MainService);
	
	/**
	 * The service to access the APP configuration.
	 */
	private readonly conf = inject(ConfigService);
	
	/**
	 * Service to access to the MOV API.
	 */
	private readonly api = inject(MovApiService);

	/**
	 * The service to show messages.
	 */
	private readonly messages = inject(MessagesService);


	/**
	 * Initialize the component.
	 */
	public ngOnInit(): void {

		this.header.changeHeaderTitle($localize`:The header title for the comnfig page@@main_topology_design_components_update_code_page-title:Update components library`);

	}

	/**
	 * Called whne is updating the library.
	 */
	public update(){
		
		this.api.refreshComponentsLibrary().subscribe(
			{
				next:()=>{
					
				},
				error:err=>this.messages.showMOVConnectionError(err)
				
			}
		);
		
	}
}
