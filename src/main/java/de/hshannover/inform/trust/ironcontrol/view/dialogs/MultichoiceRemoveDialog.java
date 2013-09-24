package de.hshannover.inform.trust.ironcontrol.view.dialogs;

import android.content.Context;
import android.net.Uri;
import de.hshannover.inform.trust.ironcontrol.R;

public class MultichoiceRemoveDialog extends MultichoiceListDialog {

	public MultichoiceRemoveDialog(Context context, Uri uri, int resIdTitle, int resIdButton) {
		super(context, uri, resIdTitle, resIdButton);
	}

	//	@Override
	//	protected void callBack(int clicked) {
	//		if (context instanceof MultichoiceSearchEvent) {
	//			MultichoiceRemoveEvent event = (MultichoiceRemoveEvent) context;
	//
	//			event.remove(getSelectedRowIds());
	//		}
	//	}

	@Override
	protected String getPositiveButtonLabel() {
		return context.getResources().getString(R.string.remove);
	}

	@Override
	protected void setNeutralButton() {}	// nothing to do

}
