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
package de.hshannover.inform.trust.ironcontrol.view.logger;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.logger.LogData;

public class LoggerPopUp extends Builder{

	Context context;
	private TextView tvDate, tvTime, tvLevel, tvMessage, tvThrowableLogo, tvThrowable;
	private View view;

	public LoggerPopUp(Context context, LogData data) {
		super(context);
		this.context = context;

		// this
		readResources();
		setText(data);
		setColor(data);

		// super
		setTitle(data.getName());
		setPositiveButton(R.string.ok, null);
		setView(view);
	}

	private void setText(LogData data) {
		tvDate.setText(getDate(data.getTime()));
		tvTime.setText(getTime(data.getTime()));
		tvLevel.setText(data.getLevel().toString());
		tvMessage.setText(data.getMessage().toString());

		if(data.getThrowable() != null){
			tvThrowable.setText(Log.getStackTraceString(data.getThrowable()));
		}else{
			tvThrowable.setVisibility(LinearLayout.GONE);
			tvThrowableLogo.setVisibility(LinearLayout.GONE);
		}
	}

	@SuppressLint("ResourceAsColor")
	private void setColor(LogData data) {
		switch(data.getLevel()){
		case ERROR :setTextColor(tvMessage, R.color.Red); setTextColor(tvThrowable, R.color.Red);
		break;
		case FATAL :setTextColor(tvMessage, R.color.DarkRed); setTextColor(tvThrowable, R.color.DarkRed);
		break;
		case DEBUG :setTextColor(tvMessage, R.color.RoyalBlue);
		break;
		case WARN :	setTextColor(tvMessage, R.color.Orange);
		break;
		case TOAST :
		case INFO : setTextColor(tvMessage, R.color.Green);
		break;
		default :	setTextColor(tvMessage, R.color.White);
		break;
		}
	}

	private void readResources(){
		LayoutInflater inflator = LayoutInflater.from(context);
		view = inflator.inflate(R.layout.logger_popup, null);

		tvDate = (TextView) view.findViewById(R.id.date);
		tvTime = (TextView) view.findViewById(R.id.time);
		tvLevel = (TextView) view.findViewById(R.id.level);
		tvMessage = (TextView) view.findViewById(R.id.message);
		tvThrowable = (TextView) view.findViewById(R.id.throwable);
		tvThrowableLogo = (TextView) view.findViewById(R.id.throwableLogo);
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
		view.setTextColor(context.getResources().getColor(colorRes));
	}
}