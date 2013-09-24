package de.hshannover.inform.trust.ironcontrol.view.irondetect;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.hshannover.inform.trust.ironcontrol.R;

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
