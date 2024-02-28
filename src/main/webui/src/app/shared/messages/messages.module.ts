/*
	Copyright 2022 UDT-IA, IIIA-CSIC

	Use of this source code is governed by GNU General Public License version 3
	license that can be found in the LICENSE file or at
	https://opensource.org/license/gpl-3-0/
*/

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageComponent } from './message.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
	declarations: [
		MessageComponent
	],
	imports: [
		CommonModule,
		MatIconModule,
		MatButtonModule,
		MatDialogModule,
		MatSnackBarModule
	],
	exports: [
		MessageComponent
	]
})
export class MessagesModule { }
