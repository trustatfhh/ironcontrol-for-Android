package de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs;

import android.content.Context;
import android.net.Uri;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

/**
 * Class for connection management
 * @author Marcel Reichenbach
 * @version %I%, %G%
 * @since 0.1
 */

public class MultichoiceSearchDialog extends MultichoiceListDialog {

	public MultichoiceSearchDialog(Context context, Uri uri, int resIdTitle, int resIdButton) {
		super(context, uri, resIdTitle, resIdButton);
	}

	//	@Override
	//	protected void callBack(int clicked) {
	//		if (context instanceof MultichoiceSearchEvent) {
	//			MultichoiceSearchEvent event = (MultichoiceSearchEvent) context;
	//
	//			event.search(getSelectedRowIds());
	//		}
	//	}

	@Override
	protected String getPositiveButtonLabel() {
		return context.getResources().getString(R.string.string_search);
	}

	@Override
	protected void setNeutralButton() {}	// nothing to do

}
