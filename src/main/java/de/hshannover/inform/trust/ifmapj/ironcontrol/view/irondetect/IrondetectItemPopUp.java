package de.hshannover.inform.trust.ifmapj.ironcontrol.view.irondetect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.util.PopUp;

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