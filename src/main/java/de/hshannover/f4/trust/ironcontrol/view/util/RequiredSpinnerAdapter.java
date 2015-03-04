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
package de.hshannover.f4.trust.ironcontrol.view.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.hshannover.f4.trust.ironcontrol.R;

/**
 * Class for RequiredSpinnerAdapter. Change the Text Color for a required element.
 * 
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class RequiredSpinnerAdapter extends PromptSpinnerAdapter {

	private Context context;
	private boolean required;

	public RequiredSpinnerAdapter(Context context, CharSequence prompt,	int textArrayResId, boolean required) {
		super(context, prompt, textArrayResId);

		this.context = context;
		this.required = required;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		if (v instanceof TextView && required) {

			((TextView)v).setTextColor(context.getResources().getColor(R.color.SteelBlue));

		}

		return v;
	}
}

