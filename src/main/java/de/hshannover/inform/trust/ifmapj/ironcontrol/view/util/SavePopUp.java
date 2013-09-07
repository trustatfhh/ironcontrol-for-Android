package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

public class SavePopUp extends PopUp{

	private Context context;

	private EditText input;

	public SavePopUp(Activity context, int messageId) {
		super(context, R.string.save, messageId);
		this.context = context;

		input = new EditText(context);
		setView(input);
	}

	@Override
	protected void callBack() {
		((PopUpEvent)context).onClickeSavePopUp(input.getText().toString());
	}
}