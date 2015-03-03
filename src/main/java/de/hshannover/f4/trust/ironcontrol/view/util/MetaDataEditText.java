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
import android.text.InputType;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.hshannover.f4.trust.ironcontrol.R;

public class MetaDataEditText extends EditText {

	public MetaDataEditText(Context context) {
		super(context);
	}

	public MetaDataEditText(Context context, String hint, int type, int w, int h) {
		super(context);
		setHint(hint);
		setTag(hint);
		setInputType(type);
		setLayoutParams(new LinearLayout.LayoutParams(w, h, 1.0f));
	}

	public MetaDataEditText(Context context, String hint, int type) {
		this(context, hint, type, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public MetaDataEditText(Context context, String hint) {
		this(context, hint, InputType.TYPE_CLASS_TEXT);
	}

	public MetaDataEditText(Context context, String hint, boolean required) {
		this(context, hint);

		if(required){
			setHintTextColor(context.getResources().getColor(R.color.SteelBlue));
		}
	}
}
