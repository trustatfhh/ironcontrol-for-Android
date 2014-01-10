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
package de.hshannover.f4.trust.ironcontrol.view.irondetect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.view.util.PopUp;

public class IrondetectItemPopUp extends PopUp{

	private Context context;

	private View view;

	private TextView etDevice, etId, etValue, etTimestamp;

	public IrondetectItemPopUp(Activity context, GuiData data) {
		super(context);

		this.context = context;

		// this
		readResources();
		setText(data);

		// super
		setView(view);
		setTitle(data.getRowCount());

	}

	private void readResources(){
		// View
		LayoutInflater inflator = LayoutInflater.from(context);
		view = inflator.inflate(R.layout.irondetect_item_popup, null);

		etDevice = (TextView) view.findViewById(R.id.device);
		etId = (TextView) view.findViewById(R.id.id);
		etValue = (TextView) view.findViewById(R.id.value);
		etTimestamp = (TextView) view.findViewById(R.id.timestamp);
	}

	private void setText(GuiData data) {

		etDevice.setText(data.getDevice());
		etId.setText(data.getId());
		etValue.setText(data.isValue());
		etTimestamp.setText(data.getTimeStamp());

	}
}