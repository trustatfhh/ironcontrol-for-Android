package de.hshannover.inform.trust.ironcontrol.view.util;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import de.hshannover.inform.trust.ironcontrol.R;

public class PopUp extends Builder{

	private Context context;

	public PopUp(Activity context, int titleId, int messageId) {
		super(context);
		this.context = context;

		// super
		setTitle(titleId);
		setMessage(messageId);
		setPositiveButton();
		setNegativeButton();
	}

	private void setNegativeButton() {
		setPositiveButton(R.string.ok, new OnClickListener() {			// callBack

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callBack();
			}
		});

	}

	private void setPositiveButton() {
		setNegativeButton(R.string.cancel, new OnClickListener() {		// Nothing, go back

			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
	}

	protected void callBack(){
		((PopUpEvent)context).onClickePopUp();
	}
}