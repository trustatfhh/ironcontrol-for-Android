/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironcontrol for android, version 1.0.2, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 - 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironcontrol.view.dialogs;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;

public class MultichoiceDialog extends Builder{

	private static final Logger logger = LoggerFactory.getLogger(MultichoiceDialog.class);

	// in mehr klassen aufteilen einer super alles dann weiter, zb publish hat dann 3 event @Overide methoden UPDATE/NOTIFY/DELETE

	private Context context;
	private int buttonType;
	private boolean[] selectedItems;
	private String[] rowLabels;
	private String[] rowIds;

	private String bPositiveLabel, bNeutralLabel, bNegativeLabel;

	public MultichoiceDialog(Context context, String[] ids, String[] labels, int buttonType) {
		super(context);
		logger.log(Level.DEBUG, "NEW...");
		this.context = context;
		this.buttonType = buttonType;
		this.selectedItems = new boolean[ids.length];
		this.rowIds = ids;
		this.rowLabels = labels;

		setMultiChoiceItems();

		bNegativeLabel = context.getResources().getString(R.string.string_abort);

		switch (buttonType) {
		case R.id.bRemove:
			setTitle(R.string.remove);
			bPositiveLabel = context.getResources().getString(R.string.remove);

			break;
		case R.id.bPublishUpdate:
			setTitle(R.string.string_update);
			setMultiPublishSettings();

			break;
		case R.id.bPublishNotify:
			setTitle(R.string.string_notify);
			setMultiPublishSettings();

			break;
		case R.id.bPublishDelete:
			setTitle(R.string.string_delete);
			setMultiPublishSettings();

			break;
		case R.id.bSearch:
			setTitle(R.string.string_search);
			bPositiveLabel = context.getResources().getString(R.string.string_search);

			break;
		case R.id.bSubscribeUpdate:
			setTitle(R.string.string_subscribe);
			setMultiSubscribeSettings();
		case R.id.filter:
			setTitle(R.string.filter);
			bPositiveLabel = context.getResources().getString(R.string.ok);

			break;
		}

		setNegativeButton();
		setPositiveButton();
		logger.log(Level.DEBUG, "...NEW");
	}

	private void setMultiPublishSettings(){
		bNeutralLabel = context.getResources().getString(R.string.multiPublish);
		bPositiveLabel = context.getResources().getString(R.string.singlePublish);
		setNeutralButton();
	}

	private void setMultiSubscribeSettings(){
		bNeutralLabel = context.getResources().getString(R.string.multiSubscrib);
		bPositiveLabel = context.getResources().getString(R.string.singleSubscrib);
		setNeutralButton();
	}

	private void setMultiChoiceItems(){
		setMultiChoiceItems(rowLabels, selectedItems, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {}});
	}

	private void setPositiveButton() {
		setPositiveButton(bPositiveLabel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int clicked) {
				callBack(clicked);
			}
		});
	}

	private void setNegativeButton() {
		setNegativeButton(bNegativeLabel, null);
	}

	private void setNeutralButton() {
		setNeutralButton(bNeutralLabel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int clicked) {
				callBack(clicked);
			}
		});
	}

	private void callBack(int clicked){
		boolean multi = false;
		switch( clicked ){
		case DialogInterface.BUTTON_POSITIVE: multi = false;
		break;
		case DialogInterface.BUTTON_NEUTRAL: multi = true;
		break;
		}
		((MultichoiceDialogEvent)context).onClickeMultichoiceDialogButton(getSelectedRowIds(), buttonType, multi);
	}

	// TODO schauen ob man das noch sch�ner bekommt
	private String[] getSelectedRowIds() {
		int count = 0;
		for (boolean selectedItem : selectedItems) {
			if (selectedItem) {
				count++;
			}
		}
		String[] selectedRowIds = new String[count];
		int ii = 0;
		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i]) {
				selectedRowIds[ii] = rowIds[i];
				ii++;
			}
		}
		return selectedRowIds;
	}
}

