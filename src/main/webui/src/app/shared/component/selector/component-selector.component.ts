/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Subscription } from 'rxjs';

import { MinComponent, MinComponentPage, MovApiService, Component as MOVComponent } from 'src/app/shared/mov-api';


export function requiredMinComponent(): ValidatorFn {

	return (control: AbstractControl): ValidationErrors | null => {

		if (control != null) {

			if (control.value == null || typeof control.value === 'string') {

				return { 'required': true };
			}
		}
		return null;
	};
}

@Component({
	selector: 'app-component-selector',
	templateUrl: './component-selector.component.html',
	styleUrls: ['./component-selector.component.css']
})
export class ComponentSelectorComponent implements OnInit, OnDestroy {

	/**
	 * The label of the selector.
	 */
	@Input()
	public label: string = '';

	/**
	 * The required error message.
	 */
	@Input()
	public requiredError: string = '';
	/**
	 * The controller for the componment name.
	 */
	public name: FormControl<MinComponent | string | null> = this.fb.control(null, { nonNullable: false, validators: [requiredMinComponent()], updateOn: 'change' });

	/**
	 * The selected component.
	 */
	@Output()
	public minComponentSelected = new EventEmitter<MOVComponent | null>();

	/**
	 * This is {@code true} if the component must have at least one subscribe channel.
	 */
	@Input()
	public hasSubscribeChannel: boolean = false;

	/**
	 * This is {@code true} if the component must have at least one publish channel.
	 */
	@Input()
	public hasPublishChannel: boolean = false;

	/**
	 * The subscription mof teh changes of the component.
	 */
	private nameChanged: Subscription | null = null;

	/**
	 * The page of possibel components.
	 */
	public page: MinComponentPage | null = null;

	/**
	 * The last selected component.
	 */
	private lastSelectedComponent: MOVComponent | null = null;

	/**
	 * Create the component.
	 */
	constructor(
		private fb: FormBuilder,
		private mov: MovApiService
	) { }


	/**
	 * Display a component.
	 */
	public displayComponentName(component: MinComponent | null | undefined): string {

		if (component != null && component.name != null) {

			return component.name;

		} else {

			return '';
		}
	}

	/**
	 * Initialize the component.
	 */
	ngOnInit() {

		this.nameChanged = this.name.valueChanges.subscribe(
			{

				next: (newValue) => this.updatePage(newValue)
			}
		);
		this.updatePage();

	}

	/**
	 * Called when has to update the page.
	 */
	private updatePage(newValue: MinComponent | string | null = null) {

		var pattern: string | null = null;
		if (newValue && typeof newValue == 'object' && newValue.name != null) {

			this.selectedComponent(newValue as MinComponent);
			pattern = "/.*" + newValue.name + ".*/i";

		} else if (typeof newValue == 'string') {

			pattern = "/.*" + newValue + ".*/i";
		}
		this.mov.getMinComponentPage(pattern, null, this.hasPublishChannel, this.hasSubscribeChannel, "name", 0, 20).subscribe(
			{
				next: page => {

					this.page = page;
					if (pattern == null && page.total == 1 && page.components != null && page.components.length > 0) {

						var component = page.components[0];
						this.selectedComponent(component);
						this.name.setValue(component, { emitEvent: false });

					} else {

						this.selectedComponent(null);
					}

				}
			}
		);
	}

	/**
	 * Unsubscribe.
	 */
	ngOnDestroy() {

		if (this.nameChanged) {

			this.nameChanged.unsubscribe();
			this.nameChanged = null;
		}

	}

	/**
	 * Called when a component is selected.
	 */
	private selectedComponent(component: MinComponent | null) {

		if (!this.macthLastSelected(component)) {

			if (component == null) {

				this.lastSelectedComponent = null;
				this.minComponentSelected.next(null);

			} else if (component instanceof MOVComponent) {

				this.lastSelectedComponent = component;
				this.minComponentSelected.next(component);

			} else {
				this.mov.getComponent(component?.id || '').subscribe(
					{
						next: found => {

							this.lastSelectedComponent = found;
							this.minComponentSelected.next(found);

						},
						error: () => {

							if (this.lastSelectedComponent != null) {

								this.lastSelectedComponent = null;
								this.minComponentSelected.next(null);
							}
						}
					}
				);
			}
		}
	}

	/**
	 * The selected component.
	 */
	@Input()
	public set minComponent(component: MOVComponent | MinComponent | null) {

		if (!this.macthLastSelected(component)) {

			if (component instanceof MOVComponent) {

				this.lastSelectedComponent = component;
				this.name.setValue(component, { emitEvent: false });

			} else {

				this.selectedComponent(component);
			}
		}

	}

	/**
	 * Check if the component with the last selected.
	 */
	private macthLastSelected(component: MinComponent | null) {

		return (this.lastSelectedComponent == component)
			|| (this.lastSelectedComponent != null && component != null && this.lastSelectedComponent.id != component.id);
	}


}
