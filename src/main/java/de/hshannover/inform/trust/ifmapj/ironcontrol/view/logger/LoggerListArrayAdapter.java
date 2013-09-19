package de.hshannover.inform.trust.ifmapj.ironcontrol.view.logger;

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
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LogData;

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
