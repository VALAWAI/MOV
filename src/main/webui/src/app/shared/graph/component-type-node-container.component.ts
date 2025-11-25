/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, EventEmitter, inject, Input, Output } from '@angular/core';
import { ComponentType } from '@shared/mov-api';

@Component({
	standalone: true,
	selector: 'app-component-type-node-container',
	imports: [
		CommonModule
	],
	templateUrl: './component-type-node-container.component.html'
})
export class ComponentTypeNodeContainerComponent implements AfterViewInit {

	/**
	 * The type of the component.
	 */
	@Input()
	public type: ComponentType = 'C0';

	/**
	 * Thi is {@code true} if the node is selected.
	 */
	@Input()
	public isSelected: boolean = false;

	/**
	 * The reference element of the container.
	 */
	private readonly elementRef = inject(ElementRef);

	/**
	 * The width of he container.
	 */
	@Output()
	public width = new EventEmitter<number>();

	/**
	 * The height of he container.
	 */
	@Output()
	public height = new EventEmitter<number>();


	/**
	 * Initialize the component.
	 */
	public ngAfterViewInit() {

		this.height.emit(
			this.elementRef.nativeElement.offsetHeight
		);
		this.width.emit(
			this.elementRef.nativeElement.offsetWidth
		);
	}

}
