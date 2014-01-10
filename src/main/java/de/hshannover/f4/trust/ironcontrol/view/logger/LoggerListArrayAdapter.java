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
package de.hshannover.f4.trust.ironcontrol.view.logger;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.hshannover.f4.trust.ironcontrol.R;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.LogData;

public class LoggerListArrayAdapter extends ArrayAdapter<LogData> {

	private static class ViewHolder {
		public TextView tvName, tvDate, tvTime, tvMessage;
	}

	public LoggerListArrayAdapter(Context context, List<LogData> objects) {
		super(context, R.layout.list_view, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// For a faster build
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			rowView = inflator.inflate(R.layout.logger_list_row, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) rowView.findViewById(R.id.name);
			viewHolder.tvDate = (TextView) rowView.findViewById(R.id.date);
			viewHolder.tvTime = (TextView) rowView.findViewById(R.id.time);
			viewHolder.tvMessage = (TextView) rowView.findViewById(R.id.message);
			rowView.setTag(viewHolder);
		}
		// Index for a inverted list
		int index = getCount() -1 -position;
		ViewHolder holder = (ViewHolder) rowView.getTag();

		setText(holder, index);
		setColor(holder, getItem(index).getLevel());
		return rowView;
	}

	private void setText(ViewHolder vh, int index) {
		vh.tvName.setText(getItem(index).getName());
		vh.tvDate.setText(getDate(getItem(index).getTime()));
		vh.tvTime.setText(getTime(getItem(index).getTime()));
		vh.tvMessage.setText(getItem(index).getMessage().toString());
	}

	@SuppressLint("ResourceAsColor")
	private void setColor(ViewHolder vh, Level l){
		switch(l){
		case ERROR:	setTextColor(vh.tvMessage, R.color.Red);
		break;
		case FATAL:	setTextColor(vh.tvMessage, R.color.DarkRed);
		break;
		case DEBUG:	setTextColor(vh.tvMessage, R.color.RoyalBlue);
		break;
		case WARN:	setTextColor(vh.tvMessage, R.color.Orange);
		break;
		case TOAST:
		case INFO:	setTextColor(vh.tvMessage, R.color.Green);
		break;
		default:	setTextColor(vh.tvMessage, R.color.White);
		break;
		}
	}

	private String getDate(long time){
		SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM", Locale.GERMANY);
		return dateFormat.format(time);
	}

	private String getTime(long time){
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
		return timeFormat.format(time);
	}

	private void setTextColor(TextView view, int colorRes){
		view.setTextColor(getContext().getResources().getColor(colorRes));
	}
}
