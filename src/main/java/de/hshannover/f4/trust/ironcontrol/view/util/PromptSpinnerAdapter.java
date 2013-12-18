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
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 Trust@HsH
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
package de.hshannover.inform.trust.ironcontrol.view.util;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Class for PromptSpinnerAdapter
 * 
 *         Decorator Adapter to allow a Spinner to show a 'Nothing Selected...'
 *         initially displayed instead of the first choice in the Adapter.
 * 
 *         Modified Class from http://de.softuses.com/108509 *
 * 
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class PromptSpinnerAdapter implements SpinnerAdapter, ListAdapter  {

	private static final int PROMPT_OFFSET = 1;

	private ArrayAdapter<CharSequence> mAdapter;

	private CharSequence prompt;

	private Context context;

	private boolean emptyList;

	protected PromptSpinnerAdapter(Context context, CharSequence prompt, int textArrayResId, List<CharSequence> data) {
		this.context = context;
		this.prompt = prompt;

		if(data != null){
			// List<CharSequence>
			if(data.isEmpty()){		// For a empty list, no item can picked

				data.add("empty");
				emptyList = true;

			}

			this.mAdapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, data);

		} else {
			// textArrayResId
			this.mAdapter = ArrayAdapter.createFromResource(context, textArrayResId, android.R.layout.simple_spinner_item);

		}

		this.mAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	/**
	 * Use this constructor to Define your 'Prompt' layout as the first
	 * row in the returned choices. If you do this, you probably don't want a
	 * prompt on your spinner or it'll have two 'Select' rows.
	 * 
	 * @param context			The Activity Context
	 * @param prompt			The first TextView in your Spinner
	 * @param textArrayResId 	The the array to use as the data source.
	 */

	public PromptSpinnerAdapter(Context context, CharSequence prompt, int textArrayResId) {
		this(context, prompt, textArrayResId, null);
	}

	/**
	 * Use this constructor to Define your 'Prompt' layout as the first
	 * row in the returned choices. If you do this, you probably don't want a
	 * prompt on your spinner or it'll have two 'Select' rows.
	 * 
	 * @param context			The Activity Context
	 * @param prompt			The first TextView in your Spinner
	 * @param data 				The list to use as the data source.
	 */

	public PromptSpinnerAdapter(Context context, CharSequence prompt, List<CharSequence> data) {
		this(context, prompt, -1, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position != 0) {

			return mAdapter.getView(position - PROMPT_OFFSET, null, parent);

		} else {
			View output = null;

			if (convertView instanceof TextView) {

				output = convertView;

			} else {

				output = new TextView(context);

			}

			((TextView) output).setText(prompt);
			((TextView) output).setTextSize(16);

			return output;
		}
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			return new View(context);
		}
		return mAdapter.getDropDownView(position - PROMPT_OFFSET, null, parent); // could re-use the convertView if possible, utilize setTag...
	}

	@Override
	public int getCount() {
		int count = mAdapter.getCount();
		return count == 0 ? 0 : count + PROMPT_OFFSET;
	}

	@Override
	public CharSequence getItem(int position) {
		return position == 0 ? "" : mAdapter.getItem(position - PROMPT_OFFSET);
	}

	@Override
	public int getItemViewType(int position) {

		// doesn't work!! Vote to Fix!
		// http://code.google.com/p/android/issues/detail?id=17128 - Spinner
		// does not support multiple view types
		// This method determines what is the convertView, this should return 1
		// for pos 0 or return 0 otherwise.

		return position == 0 ? getViewTypeCount() - PROMPT_OFFSET : mAdapter.getItemViewType(position - PROMPT_OFFSET);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount() + PROMPT_OFFSET;
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position - PROMPT_OFFSET);
	}

	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO schauen ob doch noch wieder rein nehmen, hat im logcat meldungen verursacht (glaube)
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		if(position == 0 || emptyList){	// don't allow the PROMPT-Item to be picked or list is empty
			return false;
		}
		return true;
	}
}
