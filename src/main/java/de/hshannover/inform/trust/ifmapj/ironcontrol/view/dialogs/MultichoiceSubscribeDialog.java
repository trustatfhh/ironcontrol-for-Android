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

public class MultichoiceSubscribeDialog extends MultichoiceListDialog {


	public MultichoiceSubscribeDialog(Context context, Uri uri, int resIdTitle, int resIdButton) {
		super(context, uri, resIdTitle, resIdButton);
	}

	@Override
	protected String getPositiveButtonLabel() {
		switch(resIdButton){

			case R.id.bSubscribeUpdate: return context.getResources().getString(R.string.subscribeUpdate);

			case R.id.bSubscribeDelete: return context.getResources().getString(R.string.subscribeDelete);
		}
		return null;
	}

	@Override
	protected String getNeutralButtonLabel() {
		switch(resIdButton){

			case R.id.bSubscribeUpdate: return context.getResources().getString(R.string.multiSubscrib);

			case R.id.bSubscribeDelete: return context.getResources().getString(R.string.multiDelete);
		}
		return null;
	}
}
