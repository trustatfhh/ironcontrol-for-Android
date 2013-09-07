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

public class MultichoicePublishDialog extends MultichoiceListDialog {

	public MultichoicePublishDialog(Context context, Uri uri, int resIdTitle, int resIdButton) {
		super(context, uri, resIdTitle, resIdButton);
	}

	//	@Override
	//	protected void callBack(int clicked) {
	//		if (context instanceof MultichoicePublishEvent) {
	//
	//			MultichoicePublishEvent event = (MultichoicePublishEvent) context;
	//
	//			switch(selectedButton){
	//				case R.id.bPublishUpdate: event.publishUpdate(getSelectedRowIds(), clicked);
	//				break;
	//				case R.id.bPublishNotify: event.publishNotify(getSelectedRowIds(), clicked);
	//				break;
	//				case R.id.bPublishDelete: event.publishDelete(getSelectedRowIds(), clicked);
	//				break;
	//				default: logger.fatal(context.getResources().getString(R.string.wrongButtonSelected));
	//				break;
	//			}
	//		}
	//	}

	@Override
	protected String getPositiveButtonLabel() {
		return context.getResources().getString(R.string.publish);
	}

	@Override
	protected String getNeutralButtonLabel() {
		return context.getResources().getString(R.string.multiPublish);
	}

}
