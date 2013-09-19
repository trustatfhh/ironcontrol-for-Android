package de.hshannover.inform.trust.ifmapj.ironcontrol.view.logger;

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
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LogData;

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