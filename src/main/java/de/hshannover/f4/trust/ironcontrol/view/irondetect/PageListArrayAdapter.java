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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.hshannover.f4.trust.ironcontrol.R;

public class PageListArrayAdapter extends ArrayAdapter<GuiData> {

	private static class ViewHolder {
		public TextView tvRowCount, tvDevice, tvId;
	}

	public PageListArrayAdapter(Context context, List<GuiData> objects) {
		super(context, R.layout.irondetect_list_row, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// For a faster build
		View rowView = convertView;

		if (rowView == null) {

			LayoutInflater inflator = LayoutInflater.from(getContext());
			rowView = inflator.inflate(R.layout.irondetect_list_row, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvRowCount = (TextView) rowView.findViewById(R.id.rowCount);
			viewHolder.tvDevice = (TextView) rowView.findViewById(R.id.device);
			viewHolder.tvId = (TextView) rowView.findViewById(R.id.id);
			rowView.setTag(viewHolder);

		}

		// Index for a inverted list
		int index = getCount() -1 -position;
		ViewHolder holder = (ViewHolder) rowView.getTag();

		setText(holder, index);

		if(Boolean.valueOf(getItem(index).isValue())){
			// set color
			holder.tvRowCount.setTextColor(getContext().getResources().getColor(R.color.GreenYellow));
			holder.tvDevice.setTextColor(getContext().getResources().getColor(R.color.GreenYellow));
			holder.tvId.setTextColor(getContext().getResources().getColor(R.color.GreenYellow));

		}else{
			// reset to default
			holder.tvRowCount.setTextColor(-4276546);
			holder.tvDevice.setTextColor(-4276546);
			holder.tvId.setTextColor(-4276546);

		}

		return rowView;
	}

	private void setText(ViewHolder vh, int index) {

		vh.tvRowCount.setText(getItem(index).getRowCount());
		vh.tvDevice.setText(getItem(index).getDevice());
		vh.tvId.setText(getItem(index).getId());

	}

}
